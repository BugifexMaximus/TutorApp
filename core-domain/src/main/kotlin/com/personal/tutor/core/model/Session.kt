package com.personal.tutor.core.model

import java.time.Instant
import java.util.UUID


data class ScenarioTemplate(
    val id: String,
    val title: String,
    val description: String,
    val requiredVocabTags: Set<String> = emptySet(),
    val requiredGrammarIds: Set<String> = emptySet(),
    val difficultyRange: IntRange,
    val interactionStyle: InteractionStyle
)

data class Session(
    val id: String = UUID.randomUUID().toString(),
    val startedAt: Instant,
    val endedAt: Instant? = null,
    val mode: SessionMode,
    val focusGrammarIds: List<String> = emptyList(),
    val focusVocabTags: Set<String> = emptySet(),
    val turns: List<SessionTurn> = emptyList()
)

data class SessionContext(
    val session: Session,
    val scenarioTemplate: ScenarioTemplate? = null
)

data class SessionSummary(
    val sessionId: String,
    val totalTurns: Int,
    val errors: List<DetectedError>
)

data class TutorTurnResult(
    val replyText: String,
    val vocabUsed: List<String> = emptyList(),
    val grammarUsed: List<String> = emptyList(),
    val errorsDetected: List<DetectedError> = emptyList()
)

data class SessionTurnResult(
    val updatedContext: SessionContext,
    val tutorTurn: SessionTurn
)
