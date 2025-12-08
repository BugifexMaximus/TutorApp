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
