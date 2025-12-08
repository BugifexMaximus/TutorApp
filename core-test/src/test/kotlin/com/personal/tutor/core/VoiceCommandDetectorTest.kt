package com.personal.tutor.core

import com.personal.tutor.core.model.SessionMode
import com.personal.tutor.core.services.VoiceCommand
import com.personal.tutor.core.services.VoiceCommandConfig
import com.personal.tutor.core.services.VoiceCommandDetector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class VoiceCommandDetectorTest {

    @Test
    fun `detects configured commands case insensitively`() {
        val config = VoiceCommandConfig(
            phrases = mapOf(
                VoiceCommand.Pause to listOf("pause", "hold on"),
                VoiceCommand.Resume to listOf("resume"),
                VoiceCommand.Repeat to listOf("repeat"),
                VoiceCommand.SwitchMode(SessionMode.REVIEW) to listOf("switch to review")
            )
        )
        val detector = VoiceCommandDetector(config)

        assertEquals(VoiceCommand.Pause, detector.detect("PAUSE"))
        assertEquals(VoiceCommand.SwitchMode(SessionMode.REVIEW), detector.detect("switch to review"))
        assertNull(detector.detect("unrecognized command"))
    }
}
