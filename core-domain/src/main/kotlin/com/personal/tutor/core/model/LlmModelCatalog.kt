package com.personal.tutor.core.model

/**
 * Preset LLM configurations that UI screens can surface for quick selection.
 */
data class LlmModelPreset(
    val id: String,
    val displayName: String,
    val provider: String,
    val supportsReasoning: Boolean = false,
    val systemPromptHint: String? = null
)

object LlmModelCatalog {
    val defaultPresets: List<LlmModelPreset> = listOf(
        LlmModelPreset(
            id = "gpt-5.1-thinking",
            displayName = "OpenAI 5.1 Thinking",
            provider = "OpenAI",
            supportsReasoning = true,
            systemPromptHint = "Optimize prompts for multi-step reasoning and tutoring safety rails."
        ),
        LlmModelPreset(
            id = "gemini-3",
            displayName = "Gemini 3",
            provider = "Google",
            supportsReasoning = true,
            systemPromptHint = "Enable multimodal input where available and keep replies concise."
        )
    )

    fun find(id: String): LlmModelPreset? = defaultPresets.firstOrNull { it.id == id }
}
