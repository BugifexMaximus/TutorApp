package com.personal.tutor.core.services

import com.personal.tutor.core.model.SessionMode

sealed interface VoiceCommand {
    data object Pause : VoiceCommand
    data object Resume : VoiceCommand
    data object Repeat : VoiceCommand
    data class SwitchMode(val mode: SessionMode) : VoiceCommand
}

data class VoiceCommandConfig(
    val phrases: Map<VoiceCommand, List<String>>
)

class VoiceCommandDetector(
    private val config: VoiceCommandConfig
) {
    fun detect(text: String): VoiceCommand? {
        val normalized = text.trim().lowercase()
        config.phrases.forEach { (command, options) ->
            if (options.any { normalized == it.lowercase() }) {
                return command
            }
        }
        return null
    }
}
