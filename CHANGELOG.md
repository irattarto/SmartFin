# SmartFin changelog

SmartFin is a fork of [SmartTube](https://github.com/yuliskov/SmartTube) 32.80 (with the playback fixes of 32.81), adapted for Android TV boxes, Android TVs and projectors controlled with a remote. It requires Android 7.0 or newer.

This page lists what SmartFin changed and fixed compared to the original SmartTube. The project history was condensed into a single commit, so this file is the record of the changes.

## Overview: what is different from SmartTube

### Privacy
- **SponsorBlock, privately** (off by default). SmartTube sent the ID of every video you played to the SponsorBlock server. SmartFin sends only the first 4 characters of an anonymised hash of the ID. The server can't tell which video you watch: it returns the segments of every video sharing that prefix, and the app picks the right one on the TV. No account, no user ID, nothing submitted. You can choose your own server or a mirror.
- **DeArrow removed.** It sent the ID of every video on screen to a third-party server to replace titles and thumbnails. SmartFin shows titles and thumbnails exactly as YouTube provides them.
- **Dislike count is opt-in.** The like count comes from YouTube itself. The dislike count needs Return YouTube Dislike, which receives the video ID, so it is a separate option, off by default, with a notice.
- **Google Drive backup removed.** It signed in with SmartTube's Google credentials, which the app downloaded from SmartTube's GitHub while running, and uploaded the settings folder. Local backup stays.
- Nothing is downloaded from upstream servers any more: no credentials file, and the YouTube decoding scripts are used only from the APK (SmartTube could download and run a replacement script from GitHub without checking it).
- **QR codes are generated on the TV** (sharing, About links, sign-in). SmartTube sent these links, including the sign-in link, to an online QR service.
- No other third-party image downloads. The language badge and the player's black placeholder are drawn locally instead of being loaded from flagcdn.com and via.placeholder.com.
- Crash reporting is off.
- The backup folder ID is random instead of being derived from the device's hardware ID or IMEI.
- Sign-in tokens are left out of backups, and token values are no longer written to logs.

### Playback
- Playback fixes from SmartTube 32.81: the PoToken cache resets after 60 seconds again, and long buffering caused by a PoToken error now triggers the automatic fix.
- **Audio stays in sync:** audio defaults to AAC on every device. SmartTube asked for AC-3 on 4K boxes, which in practice became Opus and drifted out of sync with the video on many TV boxes.
- The default video quality matches what the box can really play: 4K60 only when the decoder, screen and RAM can handle it, otherwise 1080p60 or 1080p30.
- Auto quality skips HDR streams on screens without HDR.
- On Android 11+ the display is told the video's frame rate when auto frame rate is on.
- Holding a key no longer flips toggles (speed toggle, like, dislike) on and off repeatedly.

### TV, remote and projector
- **Screen margins** (Settings > UI, 0–5 %), so nothing is cut off by projector keystone correction or TV overscan.
- **Low-RAM mode:** boxes with less than 1.5 GB RAM automatically get lighter image caching. Thumbnails and seek previews are cached on disk instead of being downloaded again.
- The phone-remote D-pad works on Android 13+.
- Connecting or waking a Bluetooth remote, air mouse or keyboard no longer restarts the video.
- Picture-in-picture keeps the video's aspect ratio and has a play/pause button (Android 8+ / Android TV 12+). Remote keys reach the player in picture-in-picture.
- Android TV **Continue watching** row: videos you stopped partway appear on the home screen (Android 8+).
- Channel links like `youtube.com/@name` open the channel (also broken in SmartTube).
- The notification permission is requested on Android 13+.
- QR code screens can be closed with OK or Back.

### Casting from a phone (DIAL, off by default)
- **Trusted devices:** a new phone has to be approved once on the TV (*Allow once / Always allow / Deny*). Remove them in Settings > Remote control > "Forget trusted casting phones".
- Web pages can't pair, and requests are size-, time- and rate-limited.

### Security
- Certificate checking stays on for Android 7.0 (SmartTube switched it off). Current root certificates are bundled.
- The hidden master password was removed.
- Updates come only from SmartFin's own releases, over https. They must be signed with the same key and match the SHA-256 checksum in the update manifest.
- Links from other apps can't bypass the account password, switch accounts, or inject script. Video ids are validated.
- Backup restore only brings back settings, and asks for confirmation when another app sends a backup file.
- Libraries with known vulnerabilities were updated: OkHttp 4.12, Retrofit 2.11, Gson 2.11, json-path 2.9, protobuf 3.25.5, Cronet 500 (Chromium 155), dnsjava 3.6.5. Vulnerable commons-io classes were removed.
- Proxy passwords are hidden in logs; SOCKS5 proxies with a password work.

### Under the hood
- Android 7.0 (API 24) minimum, modern build tools (AGP 9.5, Gradle 9.9, JDK 25, Kotlin 2.4).
- Release builds are shrunk with R8, with debug logging removed. About 4,000 lines of dead code were removed.
- The package name is `org.smartfin.stable`, so SmartFin installs next to SmartTube.

## Releases

### 1.1.0
- **SponsorBlock is back, privately:** off by default; only a 4-character hash prefix of the video ID is sent, never the ID; read-only, no user ID. Choose your own server or mirror and test it in the SponsorBlock settings.
- **DeArrow removed:** titles and thumbnails are shown as YouTube provides them.
- Like count from YouTube only; the dislike count (Return YouTube Dislike) is a separate option, off by default.
- **Google Drive backup removed;** local backup stays. A scheduled Drive backup and the Drive sign-in are removed from the device on first start.
- No downloads from upstream servers: no Google credentials file, no fallback decoding script.
- Fixes from SmartTube 32.81: PoToken cache reset after 60 s; long buffering after a PoToken error is fixed automatically.
- Channel names and icons for NewPipe subscription imports and offline subscriptions now come from YouTube itself (SmartTube used a Google API key downloaded from its GitHub).

### 1.0.0 (first stable release)
- SmartFin's own version numbers start here: 1.0.0, based on SmartTube 32.80. It installs as an update over the test builds.
- Same features and fixes as test build 5 (everything in the overview above).
- In-app updates are active from this release on: the app checks SmartFin's latest stable release, verifies the signature and the SHA-256 checksum.

### Test build 5 (32.80-sf5)
- Audio defaults to AAC: fixes audio drifting out of sync on many boxes.
- SponsorBlock removed; QR codes generated on the TV; no third-party image downloads; random backup folder ID.
- Picture-in-picture play/pause button; Android TV Continue watching row.
- Auto quality skips HDR on non-HDR screens; frame-rate hint on Android 11+.

### Test build 4 (32.80-sf4)
- Confirmation before a backup sent by another app replaces anything.
- Links from other apps no longer bypass the account password.
- Signed update manifest with the SHA-256 of every APK, checked by the updater.
- Smarter default video quality; picture-in-picture uses the video's aspect ratio.
- About 1.5 MB smaller per APK.

### Test build 3 (32.80-sf3)
- Screen margins setting; low-RAM mode; thumbnails cached on disk.
- `@name` channel links open the channel.
- Casting: trusted devices, protection against web pages and floods.
- Cronet 500 (Chromium 155), dnsjava 3.6.5; crashes from crafted links fixed; crash on boxes without a WebView fixed.
- R8 shrinking: about 3.5 MB smaller per APK.

### Test build 2 (32.80-sf2)
- Phone-remote arrow keys work again on Android 13+; SOCKS5 proxies with a password work; remote keys work in picture-in-picture.
- Link and remote-command validation; backups never restore sign-in tokens; logs kept in the app's own folder.
- About 4,200 lines of unused and obsolete code removed.

### Test build 1 (32.80-sf1)
- First SmartFin build: new name, icon and package; crash reporting off.
- Certificate checking kept on for Android 7.0; master password removed; tokens left out of backups; signed https updates.
- Updated network and parsing libraries.
- Phone-remote D-pad on Android 13+; no restart when a remote or keyboard connects; no repeated toggles on held keys; cached seek previews.
