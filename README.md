# Personal Voice Tutor (Kotlin)

This repository contains the JVM-only core for a personal voice tutor app with modular adapters for cloud providers. The code is organized to keep the core domain portable and testable while allowing swappable implementations for LLM, ASR, and TTS providers.

## Project layout

- `core-domain`: Pure Kotlin domain layer with models, ports, and services.
  - Models capture vocabulary, grammar, sessions, and lesson planning primitives.
  - Ports define interfaces for LLM, ASR, TTS, and persistence so providers can be swapped without touching business logic.
  - Services implement orchestration logic such as curriculum selection, tutor prompting, session progression, and command detection.
- `core-test`: Unit tests that exercise the core services with fakes and in-memory repositories.
- `infra-openai`: A placeholder OpenAI adapter that satisfies the `LlmClient` port and illustrates how provider-specific modules can plug into the core.

## Running tests

From the repository root run:

```bash
./gradlew test
```

Gradle will execute the JUnit 5 test suite across all modules.

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
