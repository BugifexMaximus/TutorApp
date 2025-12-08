package com.personal.tutor.core.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LlmModelCatalogTest {
    @Test
    fun `catalog exposes reasoning-friendly presets`() {
        val presets = LlmModelCatalog.defaultPresets

        val thinking = LlmModelCatalog.find("gpt-5.1-thinking")
        val gemini = LlmModelCatalog.find("gemini-3")

        assertThat(thinking?.displayName).isEqualTo("OpenAI 5.1 Thinking")
        assertThat(gemini?.displayName).isEqualTo("Gemini 3")
        assertThat(presets).allSatisfy { assertThat(it.supportsReasoning).isTrue() }
    }
}
