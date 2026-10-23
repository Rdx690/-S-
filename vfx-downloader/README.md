# VFX Downloader

**VFX Downloader — Powered by Sourajit VFX** is a native Android starter with a dark neon UI, URL paste/share intake, video/audio format selection, yt-dlp downloads, progress, local file list, and basic UPI payment-intent support.

## Build artifact

The Android source and the GitHub Actions workflow have produced a successful CI build. Download the APK ZIP artifact from the [successful Actions run](https://github.com/Rdx690/-S-/actions/runs/39956878404). The workflow artifact currently contains:
- `debug/app-debug.apk`: debug-signed build intended for testing.
- `release/app-release-unsigned.apk`: unsigned release build; not ready for public distribution until signed with your own key.

## Chosen foundation

- [yausername/youtubedl-android](https://github.com/yausername/youtubedl-android): Android adapter for yt-dlp. GPL-3.0 obligations apply when distributing a derivative app.
- [yt-dlp/yt-dlp](https://github.com/yt-dlp/yt-dlp): extractor/download engine with broad site coverage; compatibility can change.
- [media-downloader](https://github.com/mhogomchungu/media-downloader): Qt/C++ desktop GUI, not an Android app.
- [social-saver](https://github.com/VishalKaleria/social-saver): Electron desktop app, not a native Android foundation; review its non-commercial license before reuse.
- [social-media-downloader](https://github.com/Vette1123/social-media-downloader): Next.js PWA for public posts/share-in, not a native APK base; MIT-licensed.

## Features

- Neon Android UI branded VFX Downloader.
- Paste URL or share a URL/text from another Android app.
- Best, 720p or 480p video selection; MP3 extraction through FFmpeg.
- Progress and app-specific download folder.
- List/open downloaded files.
- UPI intent support without hard-coded merchant credentials.
- GitHub Actions APK artifacts; tagged releases can be published through the workflow.

## Limitations and payment

Site extractors may break or be blocked. Download only content you own or have permission to save; no bypass of DRM, private content, paywalls, or access controls is intended.

The support screen is a generic `upi://pay` handoff. It is **not** a FamPay merchant gateway integration, does not generate a scannable QR, and does not verify payment receipt. Real payments need provider/bank-issued merchant details, a valid QR/payment flow and server-side verification. Never trust a UPI app's client-side return alone.

The release APK is unsigned unless you configure signing secrets and add a signing step. Configure secure Android signing before public distribution. Include all required third-party license notices (especially GPL-3.0 obligations from the Android yt-dlp wrapper).
