package com.personal.tutor.core.model

import java.time.Instant


data class GrammarConcept(
    val id: String,
    val name: String,
    val description: String,
    val examples: List<ExampleSentence> = emptyList(),
    val prerequisiteIds: List<String> = emptyList(),
    val difficulty: Int,
    val tags: Set<String> = emptySet()
)

data class UserGrammarState(
    val grammarConceptId: String,
    val mastery: Float,
    val lastPracticedAt: Instant,
    val mistakeCount: Int
)
