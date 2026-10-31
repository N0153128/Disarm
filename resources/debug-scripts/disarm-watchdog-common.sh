#!/usr/bin/env bash
# Shared helpers for the disarm-watchdog-* scripts. Source this file; don't run it.
#
# Starts disarm in watchdog mode on resources/watchdog, then every INTERVAL copies
# one random test file into that directory. Stops on Ctrl+C or Ctrl+X.
# Reuses ROOT / JAR / RES from disarm-launch-common.sh.
#
# Overrides (environment):
#   DISARM_WATCH_DIR          watched directory (default: <root>/resources/watchdog)
#   DISARM_WD_STARTUP_DELAY   seconds to wait before the first drop (default: 3)

source "$(dirname "${BASH_SOURCE[0]}")/disarm-launch-common.sh"

WATCH_DIR="${DISARM_WATCH_DIR:-$RES/watchdog}"
STARTUP_DELAY="${DISARM_WD_STARTUP_DELAY:-3}"
TYPES=(audio video image text)
DROP_COUNT=0
JAVA_PID=""
STAGING=""

log() { printf '[%s] %s\n' "$(date +%H:%M:%S)" "$*"; }

# type -> directory under resources/
type_dir() {
  case "$1" in
    audio) echo "test-audio" ;;
    video) echo "test-video" ;;
    image) echo "test-img" ;;
    text)  echo "test-text" ;;
    *) return 1 ;;
  esac
}

# Accepts a plain positive integer number of seconds (no suffixes).
parse_seconds() {
  local s="$1" n
  [[ "$s" =~ ^[0-9]+$ ]] || return 1
  n=$((10#$s))
  (( n > 0 )) || return 1
  echo "$n"
}

usage() {
  local type="$1" interval="$2" me what note
  me="$(basename "$0")"
  if [[ "$type" == random ]]; then what="a random file of a random type"; else what="a random $type file"; fi
  if [[ -n "$interval" ]]; then note="default for this script: ${interval}s"; else note="required for this script"; fi
  cat <<EOF
Usage: $me -t SECONDS [disarm flags...]

Runs disarm in watchdog mode on:
  $WATCH_DIR
and copies $what into it every SECONDS seconds.

  -t, --interval SECONDS    whole seconds, e.g. -t 30 (5 min = 300, 1 h = 3600)
                            ($note)
  -h, --help                show this help
  anything else             appended to the java command after -wd,
                            e.g. -sc -su -do -dr

Stop with Ctrl+C or Ctrl+X.
EOF
}

# Picks a random file for the wanted type ("random" = random type too).
# Sets PICKED_TYPE and PICKED_FILE. Fails if there is nothing to pick.
pick_file() {
  local want="$1" type dir f tries files
  for tries in 1 2 3 4 5 6 7 8 9 10 11 12; do
    if [[ "$want" == random ]]; then
      type="${TYPES[RANDOM % ${#TYPES[@]}]}"
    else
      type="$want"
    fi
    dir="$RES/$(type_dir "$type")"
    files=()
    shopt -s nullglob
    for f in "$dir"/*; do
      [[ -f "$f" ]] && files+=("$f")
    done
    shopt -u nullglob
    if (( ${#files[@]} > 0 )); then
      PICKED_TYPE="$type"
      PICKED_FILE="${files[RANDOM % ${#files[@]}]}"
      return 0
    fi
    [[ "$want" == random ]] || break
  done
  return 1
}

# Copies a file into the watched dir under a unique name. It is staged outside the
# watched dir and then renamed into place, so disarm never sees a half-copied file.
drop_file() {
  local src="$1" base
  base="$(basename "$src")"
  DROP_COUNT=$((DROP_COUNT + 1))
  DROPPED_NAME="$(printf 'wd-%s-%04d-%s' "$(date +%H%M%S)" "$DROP_COUNT" "$base")"
  cp -- "$src" "$STAGING/$DROPPED_NAME" && mv -- "$STAGING/$DROPPED_NAME" "$WATCH_DIR/$DROPPED_NAME"
}

cleanup() {
  local _
  trap - EXIT INT TERM
  if [[ -n "$JAVA_PID" ]] && kill -0 "$JAVA_PID" 2>/dev/null; then
    kill "$JAVA_PID" 2>/dev/null
    for _ in 1 2 3 4 5 6 7 8 9 10; do
      kill -0 "$JAVA_PID" 2>/dev/null || break
      sleep 0.5
    done
    kill -9 "$JAVA_PID" 2>/dev/null
    wait "$JAVA_PID" 2>/dev/null
  fi
  [[ -n "$STAGING" ]] && rm -rf "$STAGING"
  echo
  echo "stopped: $DROP_COUNT file(s) dropped into $WATCH_DIR"
}

# watchdog_main <type|random> <default interval in seconds, or ""> [args...]
watchdog_main() {
  local type="$1" interval="$2" extra=() rc key next
  shift 2

  while (( $# )); do
    case "$1" in
      -t|--interval)
        if [[ -z "${2:-}" ]]; then
          echo "error: $1 needs a number of seconds (e.g. $1 30)" >&2
          exit 2
        fi
        if ! interval="$(parse_seconds "$2")"; then
          echo "error: bad interval '$2' (must be a whole number of seconds, e.g. 30)" >&2
          exit 2
        fi
        shift 2
        ;;
      -h|--help)
        usage "$type" "$interval"
        exit 0
        ;;
      *)
        extra+=("$1")
        shift
        ;;
    esac
  done

  if [[ -z "$interval" ]]; then
    echo "error: this script needs an interval, pass -t <seconds> (e.g. -t 30)" >&2
    echo >&2
    usage "$type" "$interval" >&2
    exit 2
  fi
  if ! command -v java >/dev/null 2>&1; then
    echo "error: java not found on PATH" >&2
    exit 1
  fi
  if [[ ! -f "$JAR" ]]; then
    echo "error: jar not found: $JAR" >&2
    exit 1
  fi
  if [[ "$type" != random && ! -d "$RES/$(type_dir "$type")" ]]; then
    echo "error: directory not found: $RES/$(type_dir "$type")" >&2
    exit 1
  fi

  mkdir -p "$WATCH_DIR"
  STAGING="$(mktemp -d "$(dirname "$WATCH_DIR")/.watchdog-staging.XXXXXX")" || exit 1
  trap cleanup EXIT
  trap 'exit 130' INT
  trap 'exit 143' TERM

  echo "=== watchdog stress: $type, every ${interval}s -> $WATCH_DIR ==="
  echo "=== java -jar $JAR $WATCH_DIR -wd ${extra[*]} ==="
  echo "=== Ctrl+C or Ctrl+X to stop ==="
  java -jar "$JAR" "$WATCH_DIR" -wd "${extra[@]}" &
  JAVA_PID=$!

  next=$((SECONDS + STARTUP_DELAY))
  while true; do
    # Wait until the next drop; poll for Ctrl+X and for disarm dying on its own.
    while (( SECONDS < next )); do
      if ! kill -0 "$JAVA_PID" 2>/dev/null; then
        wait "$JAVA_PID"
        rc=$?
        log "disarm exited on its own (exit code $rc), stopping"
        exit "$rc"
      fi
      if [[ -t 0 ]]; then
        key=""
        read -rsn1 -t 1 key
        if [[ "$key" == $'\x18' ]]; then
          log "Ctrl+X pressed, stopping"
          exit 0
        fi
      else
        sleep 1
      fi
    done

    if pick_file "$type"; then
      if drop_file "$PICKED_FILE"; then
        log "#$DROP_COUNT $PICKED_TYPE: $(basename "$PICKED_FILE") -> $DROPPED_NAME (next in ${interval}s)"
      else
        log "WARNING: failed to copy $PICKED_FILE into $WATCH_DIR"
      fi
    else
      log "WARNING: no files to pick for type '$type'"
    fi
    next=$((SECONDS + interval))
  done
}
