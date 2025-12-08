package com.personal.tutor.core.services

import com.personal.tutor.core.model.ErrorCategory
import com.personal.tutor.core.model.Session
import com.personal.tutor.core.model.SessionContext
import com.personal.tutor.core.model.SessionMode
import com.personal.tutor.core.model.SessionSummary
import com.personal.tutor.core.model.SessionTurn
import com.personal.tutor.core.model.SessionTurnResult
import com.personal.tutor.core.model.SpeakerRole
import com.personal.tutor.core.model.TurnMeta
import com.personal.tutor.core.model.ScenarioTemplate
import com.personal.tutor.core.model.TutorTurnResult
import com.personal.tutor.core.ports.ProgressRepository
import java.time.Clock

class SessionManager(
    private val tutorEngine: TutorEngine,
    private val progressRepository: ProgressRepository,
    private val clock: Clock
) {
    suspend fun startSession(
        mode: SessionMode,
        focusGrammarIds: List<String> = emptyList(),
        focusVocabTags: Set<String> = emptySet(),
        scenarioTemplate: ScenarioTemplate? = null
    ): SessionContext {
        val session = Session(
            startedAt = clock.instant(),
            mode = mode,
            focusGrammarIds = focusGrammarIds,
            focusVocabTags = focusVocabTags
        )
        return SessionContext(session = session, scenarioTemplate = scenarioTemplate)
    }

    suspend fun handleUserUtterance(
        sessionContext: SessionContext,
        userUtterance: String
    ): SessionTurnResult {
        val userTurn = SessionTurn(
            role = SpeakerRole.USER,
            text = userUtterance,
            timestamp = clock.instant()
        )

        val sessionWithUser = sessionContext.session.copy(turns = sessionContext.session.turns + userTurn)
        val userContext = sessionContext.copy(session = sessionWithUser)
        val tutorResult = tutorEngine.generateReply(userContext, userUtterance)
        val tutorTurn = buildTutorTurn(tutorResult)

        val updatedSession = sessionWithUser.copy(turns = sessionWithUser.turns + tutorTurn)
        val updatedContext = sessionContext.copy(session = updatedSession)
        applyProgressUpdate(tutorResult)
        return SessionTurnResult(updatedContext, tutorTurn)
    }

    suspend fun endSession(sessionContext: SessionContext): SessionSummary {
        val endedSession = sessionContext.session.copy(endedAt = clock.instant())
        progressRepository.saveSession(endedSession)
        return SessionSummary(
            sessionId = endedSession.id,
            totalTurns = endedSession.turns.size,
            errors = endedSession.turns.flatMap { it.meta?.errorsDetected ?: emptyList() }
        )
    }

    private fun buildTutorTurn(tutorResult: TutorTurnResult): SessionTurn {
        return SessionTurn(
            role = SpeakerRole.TUTOR,
            text = tutorResult.replyText,
            timestamp = clock.instant(),
            meta = TurnMeta(
                errorsDetected = tutorResult.errorsDetected,
                vocabUsed = tutorResult.vocabUsed,
                grammarUsed = tutorResult.grammarUsed
            )
        )
    }

    private suspend fun applyProgressUpdate(tutorResult: TutorTurnResult) {
        val lexemeStates = progressRepository.getUserLexemeState().associateBy { it.lexemeId }.toMutableMap()
        val grammarStates = progressRepository.getUserGrammarState().associateBy { it.grammarConceptId }.toMutableMap()

        val now = clock.instant()
        val grammarMistakes = tutorResult.errorsDetected.count { it.category == ErrorCategory.GRAMMAR }

        tutorResult.vocabUsed.forEach { lexemeId ->
            val current = lexemeStates[lexemeId]
            val updated = if (current == null) {
                com.personal.tutor.core.model.UserLexemeState(
                    lexemeId = lexemeId,
                    mastery = 0.1f,
                    timesSeen = 1,
                    timesCorrect = 1,
                    timesIncorrect = 0,
                    lastSeenAt = now
                )
            } else {
                current.copy(
                    mastery = (current.mastery + 0.05f).coerceAtMost(1.0f),
                    timesSeen = current.timesSeen + 1,
                    timesCorrect = current.timesCorrect + 1,
                    lastSeenAt = now
                )
            }
            lexemeStates[lexemeId] = updated
        }

        tutorResult.grammarUsed.forEach { grammarId ->
            val current = grammarStates[grammarId]
            val updated = if (current == null) {
                com.personal.tutor.core.model.UserGrammarState(
                    grammarConceptId = grammarId,
                    mastery = 0.1f,
                    lastPracticedAt = now,
                    mistakeCount = grammarMistakes
                )
            } else {
                current.copy(
                    mastery = (current.mastery + 0.05f).coerceAtMost(1.0f),
                    lastPracticedAt = now,
                    mistakeCount = current.mistakeCount + grammarMistakes
                )
            }
            grammarStates[grammarId] = updated
        }

        progressRepository.upsertUserLexemeState(lexemeStates.values.toList())
        progressRepository.upsertUserGrammarState(grammarStates.values.toList())
    }
}
