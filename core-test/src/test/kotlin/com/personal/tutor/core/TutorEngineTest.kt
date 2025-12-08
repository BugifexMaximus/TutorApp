package com.personal.tutor.core

import com.personal.tutor.core.model.Session
import com.personal.tutor.core.model.SessionContext
import com.personal.tutor.core.model.SessionMode
import com.personal.tutor.core.ports.LlmClient
import com.personal.tutor.core.ports.LlmRequest
import com.personal.tutor.core.ports.LlmResponse
import com.personal.tutor.core.services.TutorEngine
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class TutorEngineTest {

    @Test
    fun `builds request and parses payload`() = runTest {
        val client = RecordingLlmClient(
            LlmResponse(
                text = "Hola!",
                parsedPayload = mapOf(
                    "vocabUsed" to listOf("lex-1"),
                    "grammarUsed" to listOf("grammar-1"),
                    "errors" to listOf(
                        mapOf("category" to "grammar", "message" to "minor issue", "severity" to "minor")
                    )
                )
            )
        )
        val engine = TutorEngine(client, tutorStylePrompt = "Be friendly")
        val context = SessionContext(
            session = Session(startedAt = Instant.EPOCH, mode = SessionMode.CONVERSATION)
        )

        val result = engine.generateReply(context, "Hello there")

        assertEquals("Hola!", result.replyText)
        assertEquals(listOf("lex-1"), result.vocabUsed)
        assertEquals(listOf("grammar-1"), result.grammarUsed)
        assertTrue(client.lastRequest?.systemPrompt?.contains("Be friendly") == true)
        assertEquals("Hello there", client.lastRequest?.messages?.lastOrNull()?.content)
    }

    private class RecordingLlmClient(
        private val response: LlmResponse
    ) : LlmClient {
        var lastRequest: LlmRequest? = null
            private set

        override suspend fun complete(request: LlmRequest): LlmResponse {
            lastRequest = request
            return response
        }
    }
}
