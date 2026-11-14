# Installed Geometry Dash detection

## Implementation

`InstalledGameDetector.kt` performs a read-only lookup of the installed package:

- Package ID: `com.robtopx.geometryjump`
- Installed version name and version code
- Whether Android reports the app enabled
- Whether Android exposes a launcher intent
- Native library directory and whether that directory exists
- Device-supported ABIs, including whether `arm64-v8a` is supported

The detector returns one of three results: `Installed`, `NotInstalled`, or `CheckFailed`.

## Android package visibility

On Android 11 (API 30) and newer, package visibility filtering can hide other installed apps from package queries. Add this child to the app's root `<manifest>` element when creating the Android manifest:

```xml
<queries>
    <package android:name="com.robtopx.geometryjump" />
</queries>
```

The app must also declare the Android namespace on the root manifest element:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
```

## Example use

```kotlin
when (val result = InstalledGameDetector(context).detect()) {
    is InstalledGameDetector.DetectionResult.Installed -> {
        // Display detected version and compatibility information.
    }
    is InstalledGameDetector.DetectionResult.NotInstalled -> {
        // Explain that the official game is not installed.
    }
    is InstalledGameDetector.DetectionResult.CheckFailed -> {
        // Display a diagnostic message.
    }
}
```

## Important limitations

- This detector does not launch the game; it only reports whether a launcher intent exists.
- It does not clone, patch, modify, extract, or inject into the game.
- Package version and ABI are reported, but no compatibility decision is made yet.
- A non-null native library directory does not prove that a particular native library is present or loadable.
- This source has not yet been compiled because the repository still needs a complete Android Gradle application scaffold and manifest.
