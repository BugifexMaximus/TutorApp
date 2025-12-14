# Personal Voice Tutor (Kotlin)

This repository contains a modular personal voice tutor app with JVM libraries and an Android client. The code is organized to keep the core domain portable and testable while allowing swappable implementations for LLM, ASR, and TTS providers.

## Project layout

- `core-domain`: Pure Kotlin domain layer with models, ports, and services.
  - Models capture vocabulary, grammar, sessions, and lesson planning primitives.
  - Ports define interfaces for LLM, ASR, TTS, and persistence so providers can be swapped without touching business logic.
  - Services implement orchestration logic such as curriculum selection, tutor prompting, session progression, and command detection.
- `core-test`: Unit tests that exercise the core services with fakes and in-memory repositories.
- `infra-openai`: A placeholder OpenAI adapter that satisfies the `LlmClient` port and illustrates how provider-specific modules can plug into the core.

## Prerequisites and setup

- **Java:** Install JDK 21 for the JVM modules. The Android app uses AGP 8.5 and targets Java 17 bytecode, so Android Studio's
  embedded JDK 17 is sufficient for that module.
- **Gradle:** Use a local Gradle 8.7+ installation (no wrapper is checked in). Verify your `gradle` binary is on the `PATH`.
- **Android SDK (optional):** Required only if you want to build the `app` module. Install API level 34 and create an emulator
  with microphone access if you plan to test audio features. Set `ANDROID_HOME`/`ANDROID_SDK_ROOT` so Gradle can find the SDK.

Setup steps:

1. Clone the repository.
2. Ensure JDK 21 is the active JVM (`java -version`).
3. Run `gradle --version` to verify the Gradle install.
4. (Optional) Open the project in Android Studio to provision SDKs and virtual devices.

## Building and running tests

From the repository root run:

```bash
gradle test
```

Gradle will execute the JUnit 5 test suite across all modules. You can also build individual modules:

- JVM core: `gradle :core-domain:build`
- Tests only: `gradle :core-test:test`
- Android client: `gradle :app:assembleDebug`

## Using the modules

- **Core domain:** Import `core-domain` into your JVM or Android client to access the tutor models, ports, and services. The
  `LanguageCatalog` contains the starter language presets and can be extended with your own locales.
- **OpenAI adapter:** `infra-openai` offers a lightweight `OpenAiLlmClient` that satisfies the `LlmClient` port. It simulates
  responses locally so you can exercise orchestration logic without network calls.
- **Android app:** The `app` module is a Compose-based shell that depends on `core-domain`. Use Android Studio to run it on a
  device or emulator; attach your preferred LLM/ASR/TTS adapters through the ports exposed by the core.

## Supported target languages

The core ships with opinionated defaults for two starter targets:

- **Japanese (ja-JP):** polite defaults with sample greetings to seed prompts.
- **Spanish (es-ES):** neutral defaults and everyday greetings for warm-up drills.

Additional languages can be added by extending the `LanguageCatalog` in `core-domain`.

## Device and emulator testing

While this repository contains the JVM core, you can validate the tutoring loop on mobile clients that embed it:

1. **Physical device:**
   - Build your Android client that depends on the `core-domain` module and install the debug APK on a phone with developer mode enabled.
   - Grant microphone and network permissions, then point the client at your dev LLM/ASR/TTS endpoints.
   - Run a short conversational session and capture logs for latency, transcription accuracy, and prompt safety.
2. **Android emulator:**
   - In Android Studio, create an x86_64 Pixel 7 (API 34+) AVD with audio input enabled.
   - Launch the client APK or run it directly from the IDE against the emulator, ensuring text-to-speech audio playback is not muted.
   - Use the emulator’s microphone passthrough to verify wake-word detection and streaming ASR.

## LLM configuration presets

UI configuration screens should surface at least the following presets from the `LlmModelCatalog`:

- **OpenAI 5.1 Thinking** (`gpt-5.1-thinking`): optimized for multi-step reasoning and tutoring safety.
- **Gemini 3** (`gemini-3`): reasoning-friendly with guidance to keep replies concise and multimodal-ready.

Each preset includes a system prompt hint that can be shown in the UI to help users understand how the tutor will behave.
