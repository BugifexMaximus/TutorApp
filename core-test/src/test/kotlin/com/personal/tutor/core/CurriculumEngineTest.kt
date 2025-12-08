package com.personal.tutor.core

import com.personal.tutor.core.config.CurriculumConfig
import com.personal.tutor.core.model.ScenarioTemplate
import com.personal.tutor.core.model.UserGrammarState
import com.personal.tutor.core.services.CurriculumEngine
import com.personal.tutor.core.services.InMemoryProgressRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant

class CurriculumEngineTest {

    @Test
    fun `selects grammar below mastery threshold and matches scenario`() = runTest {
        val progressRepository = InMemoryProgressRepository(
            grammars = listOf(
                UserGrammarState("grammar-a", mastery = 0.3f, lastPracticedAt = Instant.EPOCH, mistakeCount = 2),
                UserGrammarState("grammar-b", mastery = 0.9f, lastPracticedAt = Instant.EPOCH, mistakeCount = 1)
            )
        )
        val scenario = ScenarioTemplate(
            id = "ordering",
            title = "Ordering food",
            description = "Practice ordering meals",
            requiredGrammarIds = setOf("grammar-a"),
            requiredVocabTags = setOf("food"),
            difficultyRange = 1..3,
            interactionStyle = com.personal.tutor.core.model.InteractionStyle.DIALOGUE
        )
        val config = CurriculumConfig(
            masteryThreshold = 0.7f,
            scenarioTemplates = listOf(scenario),
            defaultFocusVocabTags = setOf("default")
        )

        val engine = CurriculumEngine(progressRepository, config)
        val plan = engine.selectLessonPlan()

        assertEquals(listOf("grammar-a"), plan.focusGrammarIds)
        assertEquals("ordering", plan.scenarioTemplateId)
        assertEquals(setOf("default"), plan.targetVocabTags)
    }
}
