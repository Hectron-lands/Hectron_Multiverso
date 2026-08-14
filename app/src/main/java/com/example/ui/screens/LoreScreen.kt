package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import com.example.viewmodels.UniverseViewModel

@Composable
fun LoreScreen(viewModel: UniverseViewModel) {
    val loreList by viewModel.loreList.collectAsState()
    var topicText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    LaunchedEffect(loreList.size) {
        isGenerating = false
    }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            CustomHeader(universeName = "Lore Codex")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(loreList) { lore ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(SurfaceDark)
                            .border(1.dp, OutlineDark, RoundedCornerShape(24.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text("TOPIC: ${lore.topic.uppercase()}", style = MaterialTheme.typography.labelSmall, color = Cyan400)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(lore.content, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(SurfaceDark)
                    .border(1.dp, OutlineDark, RoundedCornerShape(32.dp))
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextField(
                        value = topicText,
                        onValueChange = { topicText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Topic (e.g. The Great War)...", color = TextTertiary) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = Cyan400
                        ),
                        enabled = !isGenerating
                    )
                    IconButton(
                        onClick = {
                            if (topicText.isNotBlank()) {
                                isGenerating = true
                                viewModel.generateLore(topicText)
                                topicText = ""
                            }
                        },
                        enabled = !isGenerating && topicText.isNotBlank(),
                        modifier = Modifier
                            .background(if (!isGenerating && topicText.isNotBlank()) Cyan400.copy(alpha=0.1f) else Color.Transparent, CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send, 
                            contentDescription = "Generate",
                            tint = if (!isGenerating && topicText.isNotBlank()) Cyan400 else TextTertiary
                        )
                    }
                }
            }
        }
    }
}
