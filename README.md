# Disarm
![header](header.png)

> **Java media sanitization library** — neutralize potentially malicious files by re-encoding images, audio, video, and text to safe formats through a lightweight plugin architecture.

---

## ✨ Features

| Category | Details |
|---|---|
| 🖼️ **Image** | Re-encodes JPEG, PNG, WEBP via OpenCV; optional watermark/logo overlay at 4 positions |
| 🔊 **Audio** | Re-encodes MP3, OGG, FLAC, WAV, AU, AIF via FFmpeg; native `javax.sound` fallback |
| 🎬 **Video** | Re-encodes MP4, WEBM, MKV, MOV via FFmpeg with full codec/bitrate enforcement |
| 📄 **Text** | BOM detection, HTML escaping, Unicode normalization, pattern stripping |
| 🛡️ **Validation** | Path safety, MIME whitelist, codec/bitrate/sample-rate checks, size & duration limits |
| 🔌 **Plugin system** | Each media type is a self-contained plugin — add new types without touching core |
| ⚙️ **Configurable** | Global and per-plugin config objects with typed getters/setters |
| 💻 **CLI** | PicoCLI-based command-line interface |

---

## 🚀 Quick Start

### Requirements

- Java 17+
- Maven 3.6+

### Build

```bash
# Compile
mvn compile

# Build executable uber-jar (all dependencies bundled)
mvn package

# Run
java -jar target/j-disarm-img-1.0-SNAPSHOT-uber.jar [options] <files...>

# Tests
mvn test

# Clean
mvn clean
```

### CLI Options

```
Usage: disarm [-do] [-l=<logo>] [-o=<output>] <files...>

  <files...>           Input file paths
  -o, --output         Output directory path
  -l, --logo           Path to logo/watermark file
  -do, --delete-original  Delete original file after processing
```

---

## 🏗️ Architecture

Disarm is built around a **plugin architecture** — each media type is a fully isolated plugin registered at startup. The core never knows about specific formats; it only speaks in `MediaPlugin`, `MediaConfig`, `MediaProcessor`, and `MediaValidator` interfaces.

```
MediaApp
 ├── PluginRegistry          ← maps MIME types → plugins
 ├── GlobalConfig            ← shared limits & output path
 ├── GlobalValidator         ← path safety, size, readability
 └── ProcessingContext       ← runtime state per file

plugins/
 ├── image/
 │    ├── ImagePlugin        ← registers MIME types, wires dependencies
 │    ├── ImageConfig        ← image-specific limits & watermark settings
 │    ├── ImageProcessor     ← OpenCV re-encoding & watermark logic
 │    ├── ImageValidator     ← image-level validation checks
 │    ├── ImageContext       ← runtime Mat objects & coordinates
 │    └── ImageCLI           ← CLI config for image options
 └── text/
      ├── TextPlugin
      ├── TextConfig
      ├── TextProcessor
      ├── TextValidator
      ├── TextContext
      └── TextCLI
```

### Core Interfaces

| Interface | Role |
|---|---|
| `MediaPlugin` | Entry point for each plugin — wires config, processor, validator, and registration |
| `MediaConfig` | Typed key/value store for plugin-specific settings |
| `MediaProcessor<C>` | Contains `process(Path)` — all re-encoding logic lives here |
| `MediaValidator` | Plugin-level validation before processing |
| `MediaContext` | Holds runtime data (loaded assets, titles, coordinates) |

### Processing Flow

```
fileDisarm(path)
  │
  ├─ GlobalValidator.validate()    ← path traversal, symlinks, size, readability
  │
  ├─ PluginRegistry.resolve(mime)  ← find the right plugin for this file
  │
  ├─ plugin.getValidator()         ← plugin-level checks (format, codec, duration…)
  │    └─ MediaValidator.validate()
  │
  └─ plugin.getProcessor()         ← re-encode to safe output
       └─ MediaProcessor.process()
```

---

## ⚙️ Configuration

### Global Config

`GlobalConfig` governs shared constraints applied to every file regardless of type:

```java
GlobalConfig globalConfig = new GlobalConfig();
globalConfig.setGeneralSizeLimit(3_000_000);       // 3 MB hard cap
globalConfig.setGeneralOutputPath(Path.of("out/")); // output directory
globalConfig.setKeepOriginal(true);                 // preserve input files
globalConfig.setBenchmarking(true);                 // log processing time
```

| Key | Default | Description |
|---|---|---|
| `generalSizeLimit` | 5 000 000 | Max file size in bytes |
| `generalOutputPath` | `resources/output` | Output directory |
| `keepOriginal` | `true` | Keep input file after processing |
| `benchmarking` | `false` | Log processing duration |

### Plugin Config — Image

```java
ImageConfig imageConfig = new ImageConfig();
imageConfig.setImgMaxWidth(1024);
imageConfig.setImgMaxHeight(1024);
imageConfig.setTransparency(0.7);       // watermark opacity 0.0–1.0
imageConfig.setLogoMaxWidth(80);
imageConfig.setLogoMaxHeight(80);
```

| Key | Default | Description |
|---|---|---|
| `imgMaxWidth` | 512 | Max output image width (px) |
| `imgMaxHeight` | 512 | Max output image height (px) |
| `transparency` | 0.5 | Watermark opacity |
| `logoMaxWidth` | 50 | Scaled logo width (px) |
| `logoMaxHeight` | 50 | Scaled logo height (px) |
| `logoSizeLimit` | 5 000 000 | Max logo file size (bytes) |

### Plugin Config — Text

```java
TextConfig textConfig = new TextConfig();
textConfig.setOutputEncoding("UTF-8");
textConfig.setUrlSchemes(new String[]{"javascript:", "data:", "vbscript:"});
```

| Key | Default | Description |
|---|---|---|
| `maxTextSize` | 5 000 000 | Max text file size (bytes) |
| `outputEncoding` | `UTF-8` | Output encoding (always re-encoded) |
| `normalizeForm` | `NFKC` | Unicode normalization form |
| `urlSchemes` | `javascript:` `data:` `vbscript:` | Blocked URL schemes |
| `zeroLengthChars` | U+200B U+200D U+FEFF | Zero-width chars stripped |

---

## 🔌 Adding a Plugin

New media types slot in without touching existing code:

**1.** Create a package under `plugins/yourtype/`:

```
plugins/yourtype/
  YourTypePlugin.java      ← implements MediaPlugin
  YourTypeConfig.java      ← implements MediaConfig
  YourTypeProcessor.java   ← implements MediaProcessor<YourTypeConfig>
  YourTypeValidator.java   ← implements MediaValidator
  YourTypeContext.java     ← implements MediaContext
```

**2.** Implement `MediaPlugin.register()` to push into the registry:

```java
@Override
public void register(PluginRegistry registry, MediaConfig config) {
    this.config = config;
    registry.register(config.supports(), this, "yourtype", config);
}

@Override
public Set<String> supports() {
    return Set.of("application/pdf"); // MIME types this plugin handles
}
```

**3.** Register in `DisarmPlugins.registerAll()`:

```java
YourTypeConfig cfg = new YourTypeConfig();
YourTypePlugin plugin = new YourTypePlugin();
plugin.registerGlobalConfig(globalConfig);
plugin.register(registry, cfg);
```

That's it — `MediaApp` picks it up automatically at runtime.

---

## 🛡️ Validation Pipeline

Every file passes through two validation stages before any processing occurs:

```
Stage 1 — Global (MediaApp / GlobalValidator)
  ✔ Path traversal & symlink check
  ✔ Input readability
  ✔ Output directory writability
  ✔ File non-empty
  ✔ Global size limit

Stage 2 — Plugin (MediaValidator per plugin)
  ✔ MIME type whitelist
  ✔ Format whitelist
  ✔ Codec whitelist (audio/video)
  ✔ Bitrate limits
  ✔ Sample rate limits (audio)
  ✔ Duration limits (audio/video)
```

> ⚠️ Error messages are intentionally vague to avoid leaking information to potential attackers.

---

## 📁 Supported Formats

| Type | Formats |
|---|---|
| 🖼️ Image | JPEG, PNG, WEBP |
| 🔊 Audio | MP3, OGG, FLAC, WAV, AU, AIF |
| 🎬 Video | MP4, WEBM, MKV, MOV |
| 📄 Text | TXT, LOG, plain, JSON |

### Output Naming

Processed files are given timestamped names to prevent collisions and avoid leaking original filenames:

```
{type}_{year}_{month}_{day}_{hour}_{minute}_{second}_{nano}_{ms}.{ext}
```
Example: `image_2025_11_29_0_12_35_113918339_113.png`

---

## 📦 Dependencies

| Library | Version | Purpose |
|---|---|---|
| [OpenCV](https://opencv.org/) | 4.9.0 | Image decoding, scaling, watermarking |
| [JAVE](https://github.com/a-schild/jave3) | 3.5.0 | Audio/video re-encoding via FFmpeg |
| [PicoCLI](https://picocli.info/) | — | Command-line interface |
| [Log4j2](https://logging.apache.org/log4j/2.x/) | — | Structured logging |
| [JUnit](https://junit.org/junit5/) | 5.11.0 | Unit & integration tests |

---

## 🗒️ Known Limitations (v0.1)

- UTF-32 LE BOM detection not yet implemented
- MIME detection uses `Files.probeContentType()` — magic bytes fallback planned for v0.2
- `ProcessingContext` is not thread-safe; parallel file processing is not supported
- Absolute path checking disabled for local-use v0.1; will be re-enabled with networking features
- Audio/video plugins are not yet ported to the plugin architecture (planned)

---

## 📄 License

MIT License — see `LICENSE` for details.

---

## 👤 Author

**n0153**
