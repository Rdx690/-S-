# VFX Downloader

**VFX Downloader — Powered by Sourajit VFX** is a native Android starter with a dark neon UI, link paste/share intake, video/audio format selection, yt-dlp downloads, progress, local file list, and basic UPI payment-intent support.

## Chosen foundation

- [yausername/youtubedl-android](https://github.com/yausername/youtubedl-android): Android adapter for yt-dlp, chosen for the native Android target and bundled runtime/FFmpeg integration. GPL-3.0 obligations apply when distributing a derivative app.
- [yt-dlp](https://github.com/yt-dlp/yt-dlp): extractor/download engine with broad site coverage; site compatibility may change.
- [media-downloader](https://github.com/mhogomchungu/media-downloader): Qt/C++ desktop GUI, not an Android app.
- [social-saver](https://github.com/VishalKaleria/social-saver): Electron desktop app and not a native Android foundation; review its non-commercial license before reuse.
- [social-media-downloader](https://github.com/Vette1123/social-media-downloader): a Next.js PWA for public posts and share-in, not an APK base; MIT-licensed.

## Features

- Native Android neon interface branded VFX Downloader.
- Paste URL or share a URL/text to the app.
- Best, 720p or 480p video selection; audio extraction to MP3 through FFmpeg.
- Progress and app-specific download folder.
- Open/list downloaded files.
- UPI payment intent entry point, without hard-coded merchant credentials.
- GitHub Actions workflow for debug/release APK builds and artifacts.

## Limitations

This source has not yet been built or tested on a device in this environment. Treat the app as a starter until GitHub Actions builds successfully and you verify it on an Android phone. Platform extractors can fail, require authentication, or be blocked; never bypass DRM, private media, paywalls, or access controls. Only save media you own or are authorized to download.

The UPI support screen is a generic `upi://pay` handoff. It is **not** a FamPay merchant gateway integration, does not generate a scannable QR, and does not verify receipt of payment. Real merchant use needs the provider/bank's own QR/VPA and server-side transaction verification. Do not rely on the returned UPI app UI/status as proof.

The current CI release build is not signed with your production key. For distribution, configure Android signing through Actions secrets and add a signing step. Check all dependency licenses and include required notices before release.

## Build locally

Requirements: Java 17, Android SDK API 35, and Gradle compatible with Android Gradle Plugin 8.7.3.

```bash
gradle assembleDebug
```

Debug APK output: `app/build/outputs/apk/debug/app-debug.apk`.

The Actions workflow builds debug and release APKs on pushes to `vfx-downloader`, pull requests, and manual runs. Pushing a `vfx-*` tag publishes APKs to a GitHub Release.
