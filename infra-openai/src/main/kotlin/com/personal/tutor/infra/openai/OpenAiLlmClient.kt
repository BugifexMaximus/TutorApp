package com.personal.tutor.infra.openai

import com.personal.tutor.core.ports.LlmClient
import com.personal.tutor.core.ports.LlmMessage
import com.personal.tutor.core.ports.LlmRequest
import com.personal.tutor.core.ports.LlmResponse

/**
 * Lightweight placeholder OpenAI adapter that shows how a provider can be
 * swapped in behind the LlmClient port. The implementation intentionally
 * avoids network I/O so the core module remains fully testable.
 */
class OpenAiLlmClient(
    private val model: String
) : LlmClient {
    override suspend fun complete(request: LlmRequest): LlmResponse {
        val summary = request.messages.joinToString(separator = "\n") { message ->
            "${message.role.name.lowercase()}: ${truncate(message)}"
        }
        return LlmResponse(
            text = "[$model simulated] ${request.systemPrompt}\n$summary",
            parsedPayload = emptyMap()
        )
    }

    private fun truncate(message: LlmMessage, limit: Int = 80): String {
        return if (message.content.length <= limit) message.content else message.content.take(limit) + "..."
    }
}
