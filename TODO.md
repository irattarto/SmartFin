# SmartFin TODO

The status of the audit findings and planned work. The full audit (21 Nov 2026) covered build/dependencies, security and stability/TV UX.

## Done (first test release)

- [x] Build toolchain: AGP 9.5.2, Gradle 9.9.2, JDK 25, Kotlin 2.4 (new DSL, built-in Kotlin, no Jetifier)
- [x] Own identity: `org.smartfin.*` application IDs, name, logo/banner/icon, own update channel, Crashlytics opt-in
- [x] Android 7.0 TLS: trust-all TrustManager replaced by bundled ISRG X1/X2 + SSL.com ECC roots
- [x] Hardcoded master password removed
- [x] Sign-in tokens kept out of local/GDrive/auto backups
- [x] Search provider protected (`GLOBAL_SEARCH`), backup import no longer accepts `file://`
- [x] Updater: same-signer check, https-only downloads
- [x] Proxy credentials masked in logs, answered for proxy auth only
- [x] minSdk 24, no legacy multidex
- [x] OkHttp 4.12, Retrofit 2.11, Gson 2.11, json-path 2.9 (json-smart 2.5), protobuf 3.25.5
- [x] Phone-remote D-pad works on Android 13+
- [x] Player no longer restarts when a BT remote, air mouse or keyboard connects
- [x] Held keys no longer re-fire toggles (speed toggle, like, dislike)
- [x] POST_NOTIFICATIONS requested; foreground service type for BackgroundPlaybackService
- [x] Seek preview storyboards served from disk cache
- [x] Debug builds no longer signed with the release key
- [x] Second review: SOCKS5 proxy auth, Android 13+ remote arrow keys, PIP key delivery, Conscrypt provider order
- [x] Video ids validated (deep links, remote); PoToken JS identifiers escaped; tokens kept out of logs
- [x] Backup restore filtered (no planted prefs/tokens); intent account switching blocked behind passwords
- [x] Logs and debug dumps in app-specific storage; QR payload encoded; Drive restore names validated
- [x] Dead code removed: ~80 unused classes/files, constant SDK checks for API < 24, unused build constants, dead manifest actions

## Next

### Security / dependencies
- [x] Cronet 105 -> 500.1.0 (Chromium 155); `android.uniquePackageNames=false` dropped
- [x] dnsjava 2.1.9 -> 3.6.5 with core library desugaring
- [x] commons-io copy: vulnerable XmlStreamReader & co. (CVE-2024-47554) removed (unused)
- [x] DIAL server: Host/Origin check, bounded thread pool, pairing rate limit, on-screen notice
- [x] R8 code shrinking on, logcat debug/verbose output stripped in release
- [ ] `androidx.javascriptengine` sandbox on Android 8+, keep j2v8 as fallback on 7.x
- [x] AndroidX core/core-ktx 1.13.1 (were mismatched), annotation 1.9.1; appcompat/fragment/leanback stay pinned (modded copies)
- [x] Update manifest with SHA-256 per APK (tools/make_update_manifest.py); updater verifies the hash
- [x] Backup import from other apps: copied to private cache, confirmation before anything is replaced
- [x] Resource shrinking on (themes looked up by name kept via keep.xml)
- [x] JitPack restricted to com.github.* (dependency verification metadata: later)
- [x] FileProvider shares only cache/update/ (the update APK)
- [x] Links from other apps no longer bypass the account password
- [x] Channel links by handle (youtube.com/@name) open the channel (resolve_url API; was broken upstream too)

### Privacy
- [x] SponsorBlock removed (sent every played video id to sponsor.ajay.app)
- [x] QR codes generated on the device (ZXing); upstream sent shared links and even the sign-in link to api.qrserver.com
- [x] Language button: country badge drawn locally (was a flag image from flagcdn.com)
- [x] Black player placeholder embedded (was downloaded from via.placeholder.com)
- [x] Backup folder id: random per install (was derived from ANDROID_ID / IMEI; the IMEI read could crash)
- [x] Dead installed-apps scans removed (locale guessing, package search)
- Still contacted, all optional: YouTube watch history (follows the history setting / incognito), Return YouTube Dislike (likes counter), DeArrow (off by default), chosen DoH server, GitHub updates

### Playback
- [x] Audio/video drift: default audio is AAC (mp4a) on every device; the old AC-3 default fell back to Opus/WebM, which drifts on many TV boxes. One-time migration of the saved AC-3 default
- [ ] Confirm with the reporter (device model, Android version, codec shown in stats for nerds); if it persists: Audio sync fix / Audio delay in player settings

### Android 13-15
- [ ] Storage Access Framework for backups on Android 10+ (`requestLegacyExternalStorage` is ignored on 11+)
- [ ] RemoteControlService: `connectedDevice` service type / no start from BOOT_COMPLETED (needed for targetSdk 35)
- [ ] targetSdk 35/36, 16 KB page alignment for native libs (j2v8, Cronet, conscrypt)
- [ ] Non-transitive R classes (refactor R imports), then drop `android.nonTransitiveRClass=false`

### TV / projector experience
- [x] Screen margins setting (0-5 %) for projector keystone / TV overscan
- [x] Low-RAM mode: isLowRamDevice() / < 1.5 GB RAM, Glide RGB_565 and 1-screen pools, larger thumbnail disk cache
- [x] Casting: trusted devices (approve new phones once; Forget trusted devices in settings)
- [x] Default video quality follows real decoder (size + fps), display resolution and RAM (no 4K60 on 1080p boxes)
- [x] HDR: Auto quality skips HDR streams on screens without HDR (`Display.getHdrCapabilities()`)
- [x] `Surface.setFrameRate` hint on Android 11+ (with auto frame rate enabled)
- [x] Picture-in-picture window uses the video aspect ratio (Android 8+)
- [x] PiP play/pause button (shown by the Android TV 12+ PiP menu); auto-enter deliberately off (onUserLeaveHint handles PiP)
- [x] Android TV "Continue watching" (Watch Next): in-progress videos added, finished/reset/cleared ones removed
- Media3 migration: not planned for now (large change, current player works)

### Needs a real device to test (the emulator has no PiP feature, HW decoders or HDR display)
- [ ] PiP button, Continue watching row, HDR-aware Auto, frame-rate hint, default quality on 1080p and 4K boxes
- [ ] Small follow-ups: refresh the PiP button icon when paused from the remote; clear the frame-rate hint when auto frame rate restores the display mode

### Ideas
- [ ] Separate update channel for beta builds (`smartfin_beta.json`)
