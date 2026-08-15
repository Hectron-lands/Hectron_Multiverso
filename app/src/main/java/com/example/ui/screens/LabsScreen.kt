package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abadalabs.sims.enterprise.*
import com.example.ui.theme.*

@Composable
fun LabsScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val simDatabase = remember { SimDatabase.getDatabase(context) }
    val simViewModel: SimViewModel = viewModel(
        factory = SimViewModelFactory(simDatabase.simDao())
    )
    val sims by simViewModel.sims.collectAsState(initial = emptyList())

    Scaffold(
        containerColor = BgDark,
        topBar = {
            CustomHeader(universeName = if (selectedTab == 0) "Sims Enterprise (AI & Room)" else "Multimedia Labs (AI)")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceDark,
                contentColor = Cyan400,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Sims Enterprise") },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Sims") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Multimedia Labs") },
                    icon = { Icon(Icons.Default.Science, contentDescription = "Labs") }
                )
            }

            if (selectedTab == 0) {
                SimListScreen(
                    sims = sims,
                    onAddSimClick = { name -> simViewModel.addNewSim(name) },
                    onTriggerAi = { sim -> simViewModel.triggerAiNeedUpdate(sim) }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    LabFeatureCard(
                        title = "Vision Analysis",
                        description = "Upload receipts, menus, or screenshots to extract data instantly.",
                        modelName = "gemini-2.5-flash-image",
                        buttonText = "Upload Image"
                    )
                    
                    LabFeatureCard(
                        title = "Video Summarizer",
                        description = "Analyze long video content to find key moments and generate summaries.",
                        modelName = "veo-3.1-generate-preview",
                        buttonText = "Select Video"
                    )

                    LabFeatureCard(
                        title = "Music Generation",
                        description = "Create custom soundtracks, jingles, or background music.",
                        modelName = "gemini-2.5-flash-preview-tts",
                        buttonText = "Generate Track"
                    )
                    
                    LabFeatureCard(
                        title = "Image Generation",
                        description = "Generate images perfectly suited for vertical wallpapers or horizontal banners.",
                        modelName = "gemini-3.1-flash-image-preview",
                        buttonText = "Create Artwork"
                    )
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
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
                onClick = { /* Multimedia AI Action */ },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantDark, contentColor = Cyan400),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
