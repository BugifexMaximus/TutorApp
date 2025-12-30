package com.personal.tutor.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.personal.tutor.app.ui.theme.TutorAppTheme
import com.personal.tutor.core.model.LanguageCatalog
import com.personal.tutor.core.model.LlmModelCatalog
import com.personal.tutor.core.model.LlmModelPreset
import com.personal.tutor.core.model.TargetLanguage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TutorAppTheme {
                TutorHomeScreen()
            }
        }
    }
}

@Composable
fun TutorHomeScreen() {
    val languages = remember { LanguageCatalog.defaultLanguages }
    val models = remember { LlmModelCatalog.defaultPresets }
    var selectedLanguage by remember { mutableStateOf(languages.first()) }
    var selectedModel by remember { mutableStateOf(models.first()) }

    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { HeaderSection() }
            item {
                SelectionCard(
                    title = "Target language",
                    description = "Pick a starter language for the tutor to practice.",
                ) {
                    LanguagePicker(
                        languages = languages,
                        selected = selectedLanguage,
                        onSelected = { selectedLanguage = it }
                    )
                }
            }
            item {
                SelectionCard(
                    title = "LLM preset",
                    description = "Choose a reasoning-friendly model configuration.",
                ) {
                    LlmPresetPicker(
                        presets = models,
                        selected = selectedModel,
                        onSelected = { selectedModel = it }
                    )
                }
            }
            item {
                GreetingCard(language = selectedLanguage)
            }
            item {
                PromptHintCard(preset = selectedModel)
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Personal Voice Tutor",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Select a target language and model preset to spin up a tutoring session.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun SelectionCard(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguagePicker(
    languages: List<TargetLanguage>,
    selected: TargetLanguage,
    onSelected: (TargetLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = "${selected.displayName} (${selected.code.value})",
            onValueChange = {},
            label = { Text("Language") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach { language ->
                DropdownMenuItem(
                    text = { Text("${language.displayName} (${language.code.value})") },
                    onClick = {
                        onSelected(language)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LlmPresetPicker(
    presets: List<LlmModelPreset>,
    selected: LlmModelPreset,
    onSelected: (LlmModelPreset) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            readOnly = true,
            value = selected.displayName,
            onValueChange = {},
            label = { Text("LLM preset") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            presets.forEach { preset ->
                DropdownMenuItem(
                    text = { Text(preset.displayName) },
                    onClick = {
                        onSelected(preset)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun GreetingCard(language: TargetLanguage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Warm-up greetings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (language.sampleGreetings.isEmpty()) {
                Text("No greetings configured yet for this language.")
            } else {
                language.sampleGreetings.forEach { greeting ->
                    Text("• $greeting", style = MaterialTheme.typography.bodyLarge)
                }
            }
            language.defaultFormality?.let { formality ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Default formality: $formality",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun PromptHintCard(preset: LlmModelPreset) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Preset guidance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Provider: ${preset.provider}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = if (preset.supportsReasoning) "Reasoning mode supported" else "Basic generation only",
                style = MaterialTheme.typography.bodyMedium
            )
            preset.systemPromptHint?.let { hint ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TutorHomePreview() {
    TutorAppTheme {
        TutorHomeScreen()
    }
}
