package com.personal.tutor.core.services

import com.personal.tutor.core.model.SessionContext
import com.personal.tutor.core.model.SpeakerRole
import com.personal.tutor.core.model.TutorTurnResult
import com.personal.tutor.core.ports.LlmClient
import com.personal.tutor.core.ports.LlmMessage
import com.personal.tutor.core.ports.LlmRequest
import com.personal.tutor.core.ports.LlmResponseFormat
import com.personal.tutor.core.ports.LlmRole

class TutorEngine(
    private val llmClient: LlmClient,
    private val tutorStylePrompt: String
) {
    suspend fun generateReply(
        sessionContext: SessionContext,
        userUtterance: String
    ): TutorTurnResult {
        val systemPrompt = buildSystemPrompt(sessionContext)
        val messages = buildMessages(sessionContext, userUtterance)
        val response = llmClient.complete(
            LlmRequest(
                messages = messages,
                systemPrompt = systemPrompt,
                temperature = 0.6,
                maxTokens = 400,
                responseFormat = LlmResponseFormat.JsonSchema("Return tutor reply plus lists for vocabUsed and grammarUsed.")
            )
        )
        return TutorTurnResult(
            replyText = response.text.trim(),
            vocabUsed = extractList(response.parsedPayload, "vocabUsed"),
            grammarUsed = extractList(response.parsedPayload, "grammarUsed"),
            errorsDetected = extractErrors(response.parsedPayload)
        )
    }

    private fun buildSystemPrompt(sessionContext: SessionContext): String {
        val focusGrammar = sessionContext.session.focusGrammarIds.joinToString(", ").ifEmpty { "general conversation" }
        val vocabTags = sessionContext.session.focusVocabTags.joinToString(", ").ifEmpty { "adaptive vocabulary" }
        val scenarioLine = sessionContext.scenarioTemplate?.let { "Scenario: ${it.title} - ${it.description}" } ?: ""
        return """
            You are a supportive language tutor. $tutorStylePrompt
            Focus on grammar: $focusGrammar. Target vocabulary tags: $vocabTags.
            $scenarioLine
            Respond concisely and include gentle corrections when necessary.
        """.trimIndent()
    }

    private fun buildMessages(sessionContext: SessionContext, userUtterance: String): List<LlmMessage> {
        val historyMessages = sessionContext.session.turns.map { turn ->
            val role = if (turn.role == SpeakerRole.USER) LlmRole.USER else LlmRole.ASSISTANT
            LlmMessage(role, turn.text)
        }
        return historyMessages + LlmMessage(LlmRole.USER, userUtterance)
    }

    private fun extractList(payload: Map<String, Any>?, key: String): List<String> {
        val raw = payload?.get(key) as? List<*> ?: return emptyList()
        return raw.filterIsInstance<String>()
    }

    private fun extractErrors(payload: Map<String, Any>?): List<com.personal.tutor.core.model.DetectedError> {
        val raw = payload?.get("errors") as? List<*> ?: return emptyList()
        return raw.mapNotNull { item ->
            (item as? Map<*, *>)?.let { map ->
                val category = map["category"] as? String ?: return@let null
                val message = map["message"] as? String ?: return@let null
                val severity = map["severity"] as? String ?: "MINOR"
                com.personal.tutor.core.model.DetectedError(
                    category = com.personal.tutor.core.model.ErrorCategory.valueOf(category.uppercase()),
                    message = message,
                    severity = com.personal.tutor.core.model.ErrorSeverity.valueOf(severity.uppercase())
                )
            }
        }
    }
}
