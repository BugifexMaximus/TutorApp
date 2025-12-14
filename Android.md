# Android setup on Ubuntu

This guide explains how to install every dependency needed to build and run the Android client for this project on Ubuntu 22.04 LTS or later. It covers Java, Gradle, the Android SDK/NDK, Android Studio, device configuration, and project build steps.

## 1. System prerequisites
- **OS:** Ubuntu 22.04 LTS or later with apt.
- **Hardware:** 16 GB RAM recommended for Android Studio + Emulator; at least 20 GB of free disk space.
- **Packages:** `wget`, `unzip`, `curl`, `git`, `build-essential`, and `libvirt`/`qemu-kvm` for hardware-accelerated emulation.

Install common packages:
```bash
sudo apt update
sudo apt install -y wget unzip curl git build-essential qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils
```
After installation, add your user to the `kvm` and `libvirt` groups so the emulator can use hardware virtualization:
```bash
sudo usermod -aG kvm,libvirt $USER
newgrp kvm
```

## 2. Java and Gradle
The JVM libraries use Java 21, and the Android Gradle Plugin uses Java 17 bytecode. Android Studio ships with a JDK 17 that is sufficient for the `app` module, but install JDK 21 for CLI builds.

Install OpenJDK 21 and verify:
```bash
sudo apt install -y openjdk-21-jdk
java -version
```

Install Gradle 8.7+ (no wrapper is checked in). You can use SDKMAN! for convenience:
```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install gradle 8.7
```
Confirm Gradle is on the `PATH`:
```bash
gradle --version
```

## 3. Android Studio and SDK
Android Studio bundles key tools, but you must still install platforms, build-tools, and emulator images.

1. Download the latest Android Studio for Linux from https://developer.android.com/studio.
2. Extract and install under `/opt`:
   ```bash
   sudo tar -xzf android-studio-*-linux.tar.gz -C /opt
   sudo ln -s /opt/android-studio/bin/studio.sh /usr/local/bin/android-studio
   ```
3. Launch Android Studio:
   ```bash
   android-studio
   ```
4. In the first-run wizard, choose **Standard** setup and let it install the latest SDK.

### Command-line SDK tools
Install `cmdline-tools` to manage SDK packages in headless environments:
```bash
mkdir -p "$HOME/Android/cmdline-tools"
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O /tmp/cmdline-tools.zip
unzip /tmp/cmdline-tools.zip -d /tmp/cmdline-tools-temp
mv /tmp/cmdline-tools-temp/cmdline-tools "$HOME/Android/cmdline-tools/latest"
```

### Environment variables
Add these lines to `~/.bashrc` (or `~/.zshrc`), then reload the shell:
```bash
export ANDROID_SDK_ROOT="$HOME/Android"
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools:$PATH"
```
Reload:
```bash
source ~/.bashrc
```

### Install required SDK components
Use `sdkmanager` to provision the toolchain, platform, and emulator image used by the project (API 34):
```bash
sdkmanager --licenses
sdkmanager "platform-tools" \
           "platforms;android-34" \
           "build-tools;34.0.0" \
           "cmdline-tools;latest" \
           "extras;google;google_play_services" \
           "system-images;android-34;google_apis;x86_64" \
           "emulator"
```

### (Optional) NDK for native audio
If you plan to add native audio processing, install the NDK and CMake:
```bash
sdkmanager "ndk;26.3.11579264" "cmake;3.22.1"
```

## 4. Emulator configuration
Create a hardware-accelerated emulator with microphone access:
```bash
avdmanager create avd -n Pixel7Api34 -k "system-images;android-34;google_apis;x86_64" -d pixel_7
```
Start the emulator (runs in background until closed):
```bash
emulator -avd Pixel7Api34 -netdelay none -netspeed full &
```
If microphone passthrough fails, open Android Studio > **Settings > Tools > Emulator** and ensure "Enable microphone" is checked.

## 5. Clone the project
```bash
cd ~/workspace
git clone https://github.com/your-org/TutorApp.git
cd TutorApp
```

## 6. Build from the command line
From the repo root:
```bash
# Run JVM unit tests
gradle test

# Build the Android debug APK
gradle :app:assembleDebug
```
Artifacts will appear under `app/build/outputs/apk/debug/`.

## 7. Open and run in Android Studio
1. Start Android Studio and select **Open** > the `TutorApp` directory.
2. Allow Gradle sync to finish (uses the Studio JDK by default).
3. Choose the `app` run configuration, select your emulator or physical device, and click **Run** to install and launch the app.

## 8. Physical device setup
- Enable **Developer options** and **USB debugging** on your Android phone.
- Authorize the host machine when prompted and verify connectivity:
  ```bash
  adb devices
  ```
- Grant microphone and network permissions after installing the debug APK.

## 9. Troubleshooting
- **Gradle cannot find SDK:** Ensure `ANDROID_SDK_ROOT` and `PATH` include `cmdline-tools` and `platform-tools`; re-open your shell.
- **Emulator lacks hardware acceleration:** Verify your user is in the `kvm` group and that virtualization is enabled in BIOS/UEFI.
- **Build fails on JDK version:** Confirm `java -version` shows 21 for CLI builds; Android Studio can stay on the bundled JDK 17.
- **Command not found (`sdkmanager`/`avdmanager`):** Confirm you installed `cmdline-tools` and re-exported the `PATH`.

With these steps completed, you can build, run, and debug the Android client on Ubuntu without additional dependencies.
