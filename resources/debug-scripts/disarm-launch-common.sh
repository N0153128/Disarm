#!/usr/bin/env bash
# Shared helpers for the disarm-launch-* scripts. Source this file; don't run it.
#
# Assumes the scripts live in <project root>/resources/debug-scripts/.
# Paths are resolved from the scripts' own location, so they work from any
# directory (e.g. ./resources/debug-scripts/disarm-launch-all from the project root).
# Override locations with DISARM_JAR or DISARM_RES if needed.

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
JAR="${DISARM_JAR:-$ROOT/disarm-core/disarm-core-0.2.jar}"
RES="${DISARM_RES:-$ROOT/resources}"

# launch_files <subdir> <label> <file>...
# Runs the jar on each file, prints the exit code, and never stops on a failure.
launch_files() {
  local dir="$1" label="$2"
  shift 2
  local f path rc nonzero=0 missing=0

  echo "=== $label (${#@} files) ==="
  for f in "$@"; do
    path="$RES/$dir/$f"
    if [[ ! -f "$path" ]]; then
      echo "--- $dir/$f: MISSING"
      missing=$((missing + 1))
      continue
    fi
    echo "--- $dir/$f"
    java -jar "$JAR" "$path"
    rc=$?
    echo "    exit code: $rc"
    if [[ $rc -ne 0 ]]; then nonzero=$((nonzero + 1)); fi
  done
  echo "=== $label done: $nonzero non-zero exit, $missing missing ==="
  echo
}

# launch_dir <subdir> <label>
# Runs the jar on every regular file found in resources/<subdir>, sorted by name.
launch_dir() {
  local dir="$1" label="$2"
  local files=() path

  shopt -s nullglob
  for path in "$RES/$dir"/*; do
    [[ -f "$path" ]] && files+=("$(basename "$path")")
  done
  shopt -u nullglob

  if [[ ${#files[@]} -eq 0 ]]; then
    echo "=== $label: no files found in $RES/$dir ==="
    echo
    return 1
  fi

  launch_files "$dir" "$label" "${files[@]}"
}
