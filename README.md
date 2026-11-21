# Delta Kernel Manager — v0.1.0

Phone-first, universal Android kernel manager starter with a glass-inspired UI.

## Included
- Device, Android and kernel identification.
- Read-only CPU name and thermal sensor discovery.
- Root check only after the user taps the button.
- No sysfs writes, governor changes, frequency changes or kernel modifications.

## Build from your phone using GitHub Actions
1. Open the Actions tab and run **Build Delta Kernel Manager APK**.
2. Download the `delta-kernel-manager-debug` artifact from the completed run.

## Build locally
With JDK 17 and Gradle installed, run `gradle assembleDebug`.
APK output: `app/build/outputs/apk/debug/app-debug.apk`.

Kernel interfaces vary by device and ROM. Future tuning controls must detect nodes and validate ranges before use. This starter is intentionally read-only and is not yet a production kernel manager.
