"""Create the in-app update manifest (smartfin_stable.json) for a GitHub release.

Usage (from the repo root):
    py -3 tools/make_update_manifest.py <apk_dir> <tag> <version_name> <version_code> [changelog.txt] [out.json]

The app reads https://github.com/irattarto/SmartFin/releases/latest/download/smartfin_stable.json
(the "latest" non-prerelease release), so attach the generated file to the release.
Format = upstream SmartTube manifest + SmartFin "sha256" map that the updater verifies after download.
"""
import hashlib
import json
import os
import sys

REPO = "https://github.com/irattarto/SmartFin/releases/download"
ABIS = ("armeabi-v7a", "arm64-v8a", "x86")


def sha256(path):
    digest = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def main():
    if len(sys.argv) < 5:
        print(__doc__)
        sys.exit(1)

    apk_dir, tag, version_name, version_code = sys.argv[1:5]
    changelog_path = sys.argv[5] if len(sys.argv) > 5 else None
    out_path = sys.argv[6] if len(sys.argv) > 6 else os.path.join(apk_dir, "smartfin_stable.json")

    apks = sorted(f for f in os.listdir(apk_dir) if f.endswith(".apk"))
    if not apks:
        sys.exit("No APKs found in " + apk_dir)

    package = {"sha256": {}}
    for apk in apks:
        package["sha256"][apk] = sha256(os.path.join(apk_dir, apk))
        url = "%s/%s/%s" % (REPO, tag, apk)
        abi = next((a for a in ABIS if apk.endswith("_%s.apk" % a)), None)
        if abi:
            package["downloadUrlList_" + abi] = [url]
        elif apk.endswith("_universal.apk"):
            package["downloadUrlList"] = [url]

    if "downloadUrlList" not in package:
        # devices with an ABI we don't build for fall back to armeabi-v7a
        package["downloadUrlList"] = package.get("downloadUrlList_armeabi-v7a", [])

    changelog = []
    if changelog_path:
        with open(changelog_path, encoding="utf-8") as f:
            changelog = [line.strip("-* \n") for line in f if line.strip()]

    manifest = {"package": package, version_name: {"versionCode": int(version_code), "changelog": changelog}}

    with open(out_path, "w", encoding="utf-8") as f:
        json.dump(manifest, f, indent=4, ensure_ascii=False)
    print("wrote", out_path)


if __name__ == "__main__":
    main()
