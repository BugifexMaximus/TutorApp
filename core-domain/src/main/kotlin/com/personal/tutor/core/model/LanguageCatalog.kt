package com.personal.tutor.core.model

/**
 * Opinionated defaults for target languages the tutor should handle out of the box.
 */
data class TargetLanguage(
    val code: LanguageCode,
    val displayName: String,
    val defaultFormality: String? = null,
    val sampleGreetings: List<String> = emptyList()
)

object LanguageCatalog {
    val defaultLanguages: List<TargetLanguage> = listOf(
        TargetLanguage(
            code = LanguageCode("ja-JP"),
            displayName = "Japanese",
            defaultFormality = "polite",
            sampleGreetings = listOf("こんにちは", "おはようございます")
        ),
        TargetLanguage(
            code = LanguageCode("es-ES"),
            displayName = "Spanish",
            defaultFormality = "neutral",
            sampleGreetings = listOf("Hola", "Buenos días")
        )
    )

    fun supports(languageCode: LanguageCode): Boolean =
        defaultLanguages.any { it.code == languageCode }
}
