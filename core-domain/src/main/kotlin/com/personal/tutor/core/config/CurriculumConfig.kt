package com.personal.tutor.core.config

import com.personal.tutor.core.model.ScenarioTemplate


data class CurriculumConfig(
    val masteryThreshold: Float = 0.7f,
    val reviewMistakeThreshold: Int = 3,
    val scenarioTemplates: List<ScenarioTemplate> = emptyList(),
    val defaultFocusGrammarIds: List<String> = emptyList(),
    val defaultFocusVocabTags: Set<String> = emptySet()
)
