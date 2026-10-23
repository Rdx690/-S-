# VFX Downloader

Neon-styled native Android downloader starter powered by Sourajit VFX. Android source using yausername/youtubedl-android (yt-dlp wrapper) and FFmpeg. See repository root files for licensing and build notes.

To build: install Java 17, Android SDK API 35, and Gradle 8.9+, then run `gradle assembleDebug`.

Build status: not device-tested yet. APKs should only be distributed after the GitHub Actions build succeeds and the app is tested. The UPI screen creates a UPI intent and does not verify payment or implement a FamPay merchant API.
