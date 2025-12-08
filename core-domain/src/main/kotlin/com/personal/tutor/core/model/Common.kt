package com.personal.tutor.core.model

import java.time.Instant

@JvmInline
value class LanguageCode(val value: String)

data class ExampleSentence(
    val text: String,
    val translation: String? = null
)

enum class PartOfSpeech { NOUN, VERB, ADJECTIVE, ADVERB, PARTICLE, OTHER }

enum class InteractionStyle { DIALOGUE, DRILL, QA }

enum class SessionMode { CONVERSATION, SCENARIO, DRILL, REVIEW }

enum class SpeakerRole { USER, TUTOR }

enum class ErrorCategory { GRAMMAR, VOCABULARY, PRONUNCIATION, OTHER }

enum class ErrorSeverity { MINOR, MAJOR }

data class DetectedError(
    val category: ErrorCategory,
    val message: String,
    val severity: ErrorSeverity
)

data class TurnMeta(
    val errorsDetected: List<DetectedError> = emptyList(),
    val vocabUsed: List<String> = emptyList(),
    val grammarUsed: List<String> = emptyList()
)

data class SessionTurn(
    val role: SpeakerRole,
    val text: String,
    val timestamp: Instant,
    val meta: TurnMeta? = null
)

