package com.personal.tutor.core.ports

import com.personal.tutor.core.model.Session
import com.personal.tutor.core.model.UserGrammarState
import com.personal.tutor.core.model.UserLexemeState
import kotlinx.coroutines.flow.Flow

interface LlmClient {
    suspend fun complete(request: LlmRequest): LlmResponse
}

data class LlmRequest(
    val messages: List<LlmMessage>,
    val systemPrompt: String,
    val temperature: Double? = null,
    val maxTokens: Int? = null,
    val responseFormat: LlmResponseFormat = LlmResponseFormat.PlainText
)

data class LlmMessage(
    val role: LlmRole,
    val content: String
)

enum class LlmRole { SYSTEM, USER, ASSISTANT }

sealed interface LlmResponseFormat {
    data object PlainText : LlmResponseFormat
    data class JsonSchema(val schemaDescription: String) : LlmResponseFormat
}

data class LlmResponse(
    val text: String,
    val parsedPayload: Map<String, Any>? = null
)

interface AsrClient {
    suspend fun transcribeOnce(audio: ByteArray): AsrResult
    fun transcribeStream(): AsrStream
}

data class AsrResult(
    val text: String,
    val languageCode: String? = null,
    val confidence: Double? = null
)

interface AsrStream {
    suspend fun sendAudio(chunk: ByteArray)
    suspend fun closeInput()
    fun results(): Flow<AsrResult>
}

interface TtsClient {
    suspend fun synthesize(request: TtsRequest): TtsResult
}

data class TtsRequest(
    val text: String,
    val languageCode: String,
    val voiceId: String? = null,
    val speakingRate: Double? = null,
    val pitch: Double? = null
)

data class TtsResult(
    val audioData: ByteArray,
    val audioFormat: AudioFormat
)

enum class AudioFormat { PCM16, MP3, OGG }

interface ProgressRepository {
    suspend fun getUserLexemeState(): List<UserLexemeState>
    suspend fun upsertUserLexemeState(states: List<UserLexemeState>)

    suspend fun getUserGrammarState(): List<UserGrammarState>
    suspend fun upsertUserGrammarState(states: List<UserGrammarState>)

    suspend fun saveSession(session: Session)
    suspend fun loadRecentSessions(limit: Int): List<Session>
}

interface SessionStorage {
    suspend fun save(session: Session)
    suspend fun recent(limit: Int): List<Session>
}

