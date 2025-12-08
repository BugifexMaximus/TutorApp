package com.personal.tutor.core.services

import com.personal.tutor.core.config.CurriculumConfig
import com.personal.tutor.core.model.LessonPlan
import com.personal.tutor.core.model.ScenarioTemplate
import com.personal.tutor.core.ports.ProgressRepository

class CurriculumEngine(
    private val progressRepository: ProgressRepository,
    private val curriculumConfig: CurriculumConfig
) {
    suspend fun selectLessonPlan(): LessonPlan {
        val grammarStates = progressRepository.getUserGrammarState()
        val focusGrammarIds = grammarStates
            .filter { it.mastery < curriculumConfig.masteryThreshold }
            .sortedBy { it.mastery }
            .map { it.grammarConceptId }
            .ifEmpty { curriculumConfig.defaultFocusGrammarIds }

        val scenarioTemplate = selectScenarioTemplate(focusGrammarIds)

        return LessonPlan(
            focusGrammarIds = focusGrammarIds,
            targetVocabTags = curriculumConfig.defaultFocusVocabTags,
            scenarioTemplateId = scenarioTemplate?.id
        )
    }

    private fun selectScenarioTemplate(focusGrammarIds: List<String>): ScenarioTemplate? {
        if (curriculumConfig.scenarioTemplates.isEmpty()) return null
        return curriculumConfig.scenarioTemplates.firstOrNull { template ->
            template.requiredGrammarIds.isEmpty() || template.requiredGrammarIds.any { focusGrammarIds.contains(it) }
        } ?: curriculumConfig.scenarioTemplates.first()
    }
}
