package com.personal.tutor.core.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LanguageCatalogTest {
    @Test
    fun `catalog exposes japanese and spanish defaults`() {
        val languages = LanguageCatalog.defaultLanguages

        assertThat(languages.map { it.displayName }).contains("Japanese", "Spanish")
        assertThat(LanguageCatalog.supports(LanguageCode("ja-JP"))).isTrue()
        assertThat(LanguageCatalog.supports(LanguageCode("es-ES"))).isTrue()
    }
}
