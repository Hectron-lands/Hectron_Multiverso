package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import com.example.viewmodels.UniverseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanetsScreen(viewModel: UniverseViewModel) {
    val planets by viewModel.planets.collectAsState()
    var isGenerating by remember { mutableStateOf(false) }

    // Reset generating flag when a new planet arrives
    LaunchedEffect(planets.size) {
        isGenerating = false
    }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            CustomHeader(universeName = "Cosmos Vault")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    isGenerating = true
                    viewModel.generatePlanet("Hectron")
                },
                containerColor = Cyan400,
                contentColor = BgDark,
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Generate Planet")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isGenerating) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = Cyan400,
                    trackColor = SurfaceVariantDark
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp) // Leave room for FAB
            ) {
                items(planets) { planet ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(32.dp))
                            .background(SurfaceDark)
                            .border(1.dp, OutlineDark, RoundedCornerShape(32.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(planet.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Class: ${planet.type.uppercase()}", style = MaterialTheme.typography.labelSmall, color = Cyan400)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(SurfaceVariantDark, CircleShape)
                                        .border(1.dp, OutlineDark, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Placeholder for planet thumbnail
                                    Box(modifier = Modifier.size(24.dp).background(Purple600, CircleShape))
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(planet.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ResourceChip(label = "IRON", value = planet.iron.toString(), color = TextSecondary)
                                ResourceChip(label = "GOLD", value = planet.gold.toString(), color = GoldAccent)
                                ResourceChip(label = "ENERGY", value = planet.energy.toString(), color = Cyan400)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.ResourceChip(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .weight(1f)
            .background(SurfaceVariantDark, RoundedCornerShape(12.dp))
            .border(1.dp, OutlineDark, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = color, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, color = TextPrimary, style = MaterialTheme.typography.labelMedium)
        }
    }
}
