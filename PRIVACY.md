# SmartFin privacy

SmartFin has no servers of its own and collects nothing about you: no analytics, no telemetry, no crash reporting. Everything the app stores (settings, sign-in tokens, history position, backups) stays on the device unless you copy a backup somewhere yourself.

This page lists every service the app contacts, and what each one receives.

## Always

| Service | When | What it receives |
|---|---|---|
| **YouTube / Google** | Browsing, search, playback, sign-in | What any YouTube app sends: your requests, and your account token if you signed in. Watch history follows your YouTube history setting. |
| **GitHub** (`github.com/irattarto/SmartFin` releases) | Update check (stable and beta builds; not the F-Droid build) | A normal download request (your IP address). No device or account data. |

## Only if you turn them on (all off by default)

| Feature | Service | What it receives |
|---|---|---|
| **SponsorBlock** | `sponsor.ajay.app`, or the server you choose | Only the first 4 characters of the SHA-256 hash of the video ID, never the ID itself. Each such prefix is shared by a very large number of YouTube videos, so the server can't tell which one you watch; the app picks the right one on the device. Read-only: no account, no user ID, nothing is submitted. |
| **Dislike count** | `returnyoutubedislikeapi.com` | The ID of each video you play. The settings screen says so next to the option. |
| **Google DNS** (DNS setting) | Google public DNS | The host names the app connects to. |
| **Casting from a phone** (DIAL) | Phones on your local network | Pairing only. New phones must be approved on the TV. |

## Never

- No advertising IDs, installed-app lists, hardware IDs or IMEI are read or sent. The backup folder ID is random.
- No third-party image or QR-code services: QR codes and placeholders are drawn on the device.
- No code or settings are downloaded from anyone else's server: the YouTube decoding scripts ship inside the APK.
- Removed from the original SmartTube: DeArrow (sent every video ID on screen) and Google Drive backup (signed in with SmartTube's own Google credentials, downloaded from SmartTube's GitHub).

## Links

Links in the app (About, donations, SponsorBlock pages) open only when you select them, in the app's built-in web view or a browser. From then on, that website's own privacy policy applies.

## Questions

Please open an issue at <https://github.com/irattarto/SmartFin/issues>.
