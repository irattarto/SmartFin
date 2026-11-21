<p align="center"><img src="images/smartfin_banner.png" alt="SmartFin" width="480"></p>

# SmartFin

SmartFin is a YouTube client for **Android TV boxes, Android TVs and Android projectors** that you control with a remote. It is a fork of [SmartTube](https://github.com/yuliskov/SmartTube) by yuliskov. It keeps almost all of SmartTube's features, adds security fixes, and is tuned for remote control on TV boxes and projectors.

> **Status: stable, version 1.0.0** (based on SmartTube 32.80). Download it from [Releases](https://github.com/irattarto/SmartFin/releases/latest); the app updates itself from there. Please report problems in [Issues](https://github.com/irattarto/SmartFin/issues).

## Compatibility

| | |
|---|---|
| **Android version** | **7.0 (API 24) or newer**, tested on Android 7.0 and Android 16 TV emulators |
| **Devices** | Android TV / Google TV, Android TV boxes, Android projectors, Fire TV (Fire OS 6 or newer), NVIDIA Shield, Chromecast with Google TV |
| **CPU (APK to pick)** | `armeabi-v7a`: most TV boxes and projectors (32-bit ARM). `arm64-v8a`: newer 64-bit-only devices. `x86`: Intel boxes and emulators. `universal`: works everywhere but is the largest download |
| **Input** | D-pad remote, air mouse, Bluetooth keyboard, gamepad, phone remote (casting) |
| **Not supported** | Phones and tablets; Android 4.x-6.x (use upstream SmartTube there); Samsung Tizen and LG webOS TVs (not Android) |
| **Package name** | `org.smartfin.stable`. It installs **alongside** SmartTube without replacing it |

## Installation

1. Download the APK for your device from [Releases](https://github.com/irattarto/SmartFin/releases). If you are unsure which one you need, pick `armeabi-v7a`.
2. Copy it to the TV box, for example with the *Downloader* app, a USB stick, *Send Files to TV* or `adb install`.
3. Allow installing apps from unknown sources when Android asks.

Release APKs are signed with the SmartFin key. Certificate SHA-256:
`d9b4ed8aa4dcf4effd57ecc4cd195d52a52aaa4009e6364a52d5747a0c5b757c`.
You can check it with `apksigner verify --print-certs SmartFin_*.apk`.

## What's different from SmartTube

**Security**
- On Android 7.0, certificate checking is no longer switched off. Upstream trusted *every* TLS certificate on Android ≤ 7.0. SmartFin keeps validation on and only adds the three root certificates that 7.0 lacks (ISRG Root X1/X2, SSL.com ECC).
- The hidden master password that unlocked the app and account locks has been removed.
- Google sign-in tokens are left out of backups (local, Google Drive, Android auto backup). After restoring a backup, sign in again.
- Updates are offered only if the downloaded APK is signed with the same key as the installed app, and only over https.
- The search provider can only be read by the system search. The backup import no longer accepts `file://` links.
- Proxy passwords are masked in logs and given only to the proxy itself.
- Crash reporting is off. It is included only if you build with your own Firebase project.
- Outdated libraries with known vulnerabilities were updated: OkHttp 4.12, Gson 2.11, json-path 2.9, protobuf 3.25.5, Retrofit 2.11, Cronet 500 (Chromium 155), dnsjava 3.6.
- Casting from a phone (DIAL, off by default): a new phone must be approved once on the TV (*Allow once / Always allow / Deny*). Web pages cannot pair, and requests are size- and rate-limited.
- Links from other apps and remote commands are validated, so crafted links or malformed commands can no longer crash the app or inject script.

**TV, remote and projector**
- The phone remote's D-pad, OK and Back work again on Android 13+.
- Connecting or waking a Bluetooth remote, air mouse or keyboard no longer restarts the video.
- Holding a key no longer flips toggles (speed toggle, like/dislike) on and off repeatedly. Volume and speed up/down still repeat while held.
- Seek preview thumbnails are cached instead of being downloaded again on every step.
- The notification permission is requested on Android 13+, so reminders and remote-control notifications work.
- New **Screen margins** setting (Settings > UI, 0–5 %). It keeps everything visible on projectors with keystone correction and on TVs that cut off the edges.
- Low-RAM mode: boxes with less than 1.5 GB RAM get lighter image caching automatically.
- Channel links like `youtube.com/@name` open the channel. This was broken in upstream SmartTube too.

**Privacy and playback**
- SponsorBlock was removed. It sent the ID of every video you played to a third-party server.
- QR codes (sharing, About, sign-in) are generated on the device. Upstream sent the links, including the sign-in link, to an online QR service.
- No other third-party image downloads: the language badge and the player's black placeholder are drawn locally.
- The backup folder ID is random instead of being derived from the device's hardware ID.
- Audio defaults to AAC on every device. Upstream's AC-3 default usually ended up as Opus, which drifts out of sync with the video on many TV boxes.

**Build**
- Android Gradle Plugin 9.5, Gradle 9.9, JDK 25 and Kotlin 2.4. Minimum SDK 24. Release builds are shrunk with R8, and about 4,000 lines of dead code were removed.

All changes and fixes compared to SmartTube, build by build, are in [CHANGELOG.md](CHANGELOG.md). The full list of planned work is in [TODO.md](TODO.md).

## Features (inherited from SmartTube)

The interface is built for TVs. Other features:
- DeArrow (optional, off by default)
- Adjustable playback speed
- Resolutions up to 8K, 60 fps and HDR
- Live chat
- Customizable buttons
- Casting from the YouTube phone app
- Picture-in-picture
- Voice search
- Backup and restore
- No Google Services required

See the [original SmartTube README](docs/UPSTREAM_README.md) for the full feature description.

## Building

Requirements: JDK 21–25 (Android Studio's bundled JBR works), Android SDK with platform 34 and build-tools 36.

```bash
git clone --recurse-submodules https://github.com/irattarto/SmartFin.git
cd SmartFin
./gradlew assembleStstableDebug          # debug APKs in smarttubetv/build/outputs/apk/ststable/debug
```

Release builds need a `keystore.properties` file in the repository root. It is git-ignored and must never be committed:

```properties
storeFile=/path/to/your-release.jks
storePassword=...
keyAlias=...
keyPassword=...
```

App icons are generated from the artwork in `Logo/` with `py -3 tools/make_icons.py`.

**In-app updates.** Attach `smartfin_stable.json` to each stable (non-pre-release) GitHub release. Generate it with:

```bash
py -3 tools/make_update_manifest.py <apk_dir> <tag> <versionName> <versionCode> [changelog.txt]
```

The app reads `releases/latest/download/smartfin_stable.json`, so only the latest stable release counts. It offers an update only when the downloaded APK matches a SHA-256 hash listed in the manifest *and* is signed with the same certificate as the installed app.

## Credits and license

SmartFin is based on [SmartTube](https://github.com/yuliskov/SmartTube) by **yuliskov** and its contributors, and is released under the same [MIT License](LICENSE).
SmartFin is not affiliated with or endorsed by Google or YouTube.
