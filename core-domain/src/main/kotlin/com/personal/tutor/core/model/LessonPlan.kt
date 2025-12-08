package com.personal.tutor.core.model


data class LessonPlan(
    val focusGrammarIds: List<String>,
    val targetVocabTags: Set<String>,
    val scenarioTemplateId: String? = null
)
