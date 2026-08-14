package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun LabsScreen() {
    Scaffold(
        containerColor = BgDark,
        topBar = {
            CustomHeader(universeName = "Multimedia Labs (AI)")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LabFeatureCard(
                title = "Vision Analysis",
                description = "Upload receipts, menus, or screenshots to extract data instantly.",
                modelName = "gemini-1.5-pro",
                buttonText = "Upload Image"
            )
            
            LabFeatureCard(
                title = "Video Summarizer",
                description = "Analyze long video content to find key moments and generate summaries.",
                modelName = "gemini-1.5-pro",
                buttonText = "Select Video"
            )

            LabFeatureCard(
                title = "Music Generation",
                description = "Create custom soundtracks, jingles, or background music.",
                modelName = "lyria-3-clip-preview",
                buttonText = "Generate Track"
            )
            
            LabFeatureCard(
                title = "Image Generation",
                description = "Generate images perfectly suited for vertical wallpapers or horizontal banners.",
                modelName = "gemini-3.1-flash-image",
                buttonText = "Create Artwork"
            )
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun LabFeatureCard(title: String, description: String, modelName: String, buttonText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceDark)
            .border(1.dp, OutlineDark, RoundedCornerShape(24.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(modelName, style = MaterialTheme.typography.labelSmall, color = Purple600)
            Spacer(modifier = Modifier.height(12.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* TODO: Implement actual AI calls */ },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = Cyan400),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
