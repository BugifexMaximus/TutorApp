package com.personal.tutor.core

import com.personal.tutor.core.model.SessionMode
import com.personal.tutor.core.ports.LlmClient
import com.personal.tutor.core.ports.LlmRequest
import com.personal.tutor.core.ports.LlmResponse
import com.personal.tutor.core.services.InMemoryProgressRepository
import com.personal.tutor.core.services.SessionManager
import com.personal.tutor.core.services.TutorEngine
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class SessionManagerTest {

    @Test
    fun `handles a user utterance and updates progress`() = runTest {
        val clock = Clock.fixed(Instant.parse("2024-01-01T00:00:00Z"), ZoneOffset.UTC)
        val progressRepository = InMemoryProgressRepository()
        val tutorEngine = TutorEngine(FakeLlmClient(), tutorStylePrompt = "Direct")
        val manager = SessionManager(tutorEngine, progressRepository, clock)

        val context = manager.startSession(mode = SessionMode.CONVERSATION)
        val result = manager.handleUserUtterance(context, "Hi there")
        val summary = manager.endSession(result.updatedContext)

        assertEquals(2, result.updatedContext.session.turns.size) // user + tutor
        val vocabState = progressRepository.getUserLexemeState().single { it.lexemeId == "lexeme-1" }
        assertEquals(1, vocabState.timesSeen)
        val grammarState = progressRepository.getUserGrammarState().single { it.grammarConceptId == "grammar-1" }
        assertEquals(0, grammarState.mistakeCount)
        assertEquals(2, summary.totalTurns)
    }

    private class FakeLlmClient : LlmClient {
        override suspend fun complete(request: LlmRequest): LlmResponse {
            return LlmResponse(
                text = "Tutor reply",
                parsedPayload = mapOf(
                    "vocabUsed" to listOf("lexeme-1"),
                    "grammarUsed" to listOf("grammar-1"),
                    "errors" to emptyList<Any>()
                )
            )
        }
    }
}
