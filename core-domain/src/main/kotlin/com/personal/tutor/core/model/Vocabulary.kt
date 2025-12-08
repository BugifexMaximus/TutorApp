package com.personal.tutor.core.model

import java.time.Instant


data class Lexeme(
    val id: String,
    val language: LanguageCode,
    val baseForm: String,
    val reading: String? = null,
    val partOfSpeech: PartOfSpeech,
    val difficulty: Int,
    val tags: Set<String> = emptySet()
)

data class UserLexemeState(
    val lexemeId: String,
    val mastery: Float,
    val timesSeen: Int,
    val timesCorrect: Int,
    val timesIncorrect: Int,
    val lastSeenAt: Instant,
    val exampleSentences: List<ExampleSentence> = emptyList()
)
