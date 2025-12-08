package com.personal.tutor.core.services

import com.personal.tutor.core.model.Session
import com.personal.tutor.core.model.UserGrammarState
import com.personal.tutor.core.model.UserLexemeState
import com.personal.tutor.core.ports.ProgressRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryProgressRepository(
    lexemes: List<UserLexemeState> = emptyList(),
    grammars: List<UserGrammarState> = emptyList()
) : ProgressRepository {

    private val mutex = Mutex()
    private val lexemeStates: MutableMap<String, UserLexemeState> = lexemes.associateBy { it.lexemeId }.toMutableMap()
    private val grammarStates: MutableMap<String, UserGrammarState> = grammars.associateBy { it.grammarConceptId }.toMutableMap()
    private val sessions: MutableList<Session> = mutableListOf()

    override suspend fun getUserLexemeState(): List<UserLexemeState> = mutex.withLock {
        lexemeStates.values.toList()
    }

    override suspend fun upsertUserLexemeState(states: List<UserLexemeState>) {
        mutex.withLock {
            states.forEach { lexemeStates[it.lexemeId] = it }
        }
    }

    override suspend fun getUserGrammarState(): List<UserGrammarState> = mutex.withLock {
        grammarStates.values.toList()
    }

    override suspend fun upsertUserGrammarState(states: List<UserGrammarState>) {
        mutex.withLock {
            states.forEach { grammarStates[it.grammarConceptId] = it }
        }
    }

    override suspend fun saveSession(session: Session) {
        mutex.withLock { sessions.add(session) }
    }

    override suspend fun loadRecentSessions(limit: Int): List<Session> = mutex.withLock {
        sessions.takeLast(limit)
    }
}
