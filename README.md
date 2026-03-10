# Disarm
Java library for sanitizing potentially malicious media files by re-encoding images, audio, video, and text to safe formats with configurable validation and size limits.         
                                                                                                                                                                                                                               
  ---                                                                                                                                                                                                                          
  Features        
          
  - Image sanitization — re-encodes JPEG, PNG, WEBP; optional watermark/logo overlay
  - Audio sanitization — re-encodes MP3, OGG, FLAC, WAV, AU, AIF via FFmpeg with native fallback                                                                                                                               
  - Video sanitization — re-encodes MP4, WEBM, MKV, MOV via FFmpeg                                                                                                                                                             
  - Text sanitization — BOM detection, HTML escaping, Unicode normalization, pattern stripping                                                                                                                                 
  - Multi-layer validation — path safety, MIME whitelist, codec/bitrate/sample rate checks, size and duration limits                                                                                                           
  - Configurable — all limits and constraints adjustable via fluent builder API                                                                                                                                                
  - CLI support — PicoCLI-based command-line interface
                                                                                                                                                                                                                               
  ---             
  Requirements

  - Java 17+
  - Maven 3.6+

  ---                                                                                                                                                                                                                          
  Build
                                                                                                                                                                                                                               
  # Compile       
  mvn compile

  # Build executable uber-jar                                                                                                                                                                                                  
  mvn package
                                                                                                                                                                                                                               
  # Run tests     
  mvn test

  # Clean
  mvn clean

  The uber-jar with all dependencies is output to:
  target/disarm-1.0-SNAPSHOT-uber.jar

  ---                                                                                                                                                                                                           
                  
  Custom configuration                                                                                                                                                                                                         
```
DisarmConfig config = DisarmConfig.builder()

      .setGeneralOutputPath("output/")

      .setAudioMaxDuration(600_000)   // 10 minutes in ms

      .setImgMaxWidth(1024)        

      .setImgMaxHeight(1024)        

      .setKeepOriginal(true)        

      .build();
  ```
   
  ---                                                                                                                                                                                                          
  Validation Pipeline

  Every file goes through the following checks before processing:

  1. Path safety — traversal and symlink detection                                                                                                                                                                             
  2. MIME type whitelist
  3. File size limit                                                                                                                                                                                                           
  4. Codec whitelist
  5. Bitrate limits                                                                                                                                                                                                            
  6. Sample rate limits (audio/video)
  7. Duration limit (audio/video)                                                                                                                                                                                              
                  
  ---                                                                                                                                                                                                          
  Usage options:                                                                                                                                                                                                               
  - -o / --output — Output directory path
  - -l / --logo — Path to logo/watermark file                                                                                                                                                                                  
  - -do / --delete-original — Delete original file after processing
  - -da / --debug-all — Enable debug logging                                                                                                                                                                                   
                  
  Supported formats:                                                                                                                                                                                                           
  - Image: JPEG, PNG, WEBP
  - Audio: MP3, OGG, FLAC, WAV, AU, AIF                                                                                                                                                                                        
  - Video: MP4, WEBM, MKV, MOV         
  - Text: TXT, LOG, plain                                                                                                                                                                                                      
                                                                                                                                                                                                                               
  Dependencies:                                                                                                                                                                                                                
  - OpenCV 4.9.0 — Image processing and watermarking                                                                                                                                                                           
  - JAVE 3.5.0 — Audio/video re-encoding via FFmpeg                                                                                                                                                                            
  - PicoCLI — Command-line interface               
  - Log4j2 — Logging                                                                                                                                                                                                           
  - JUnit 5.11.0 — Testing

  ---
  
  License         

  MIT License — see LICENSE for details.

  ---
  Author
        
  n0153
