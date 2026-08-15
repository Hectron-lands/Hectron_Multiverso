package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import com.example.viewmodels.EcsViewModel

@Composable
fun SimulationScreen(ecsViewModel: EcsViewModel = viewModel()) {
    val profiles by ecsViewModel.engine.profiles.collectAsState()
    val engineLog by ecsViewModel.engine.engineLog.collectAsState()
    val renderState by ecsViewModel.engine.renderState.collectAsState()
    val telemetry by ecsViewModel.engine.telemetry.collectAsState()
    
    var isRunning by remember { mutableStateOf(true) }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            CustomHeader(universeName = "HECTRON-Ψ Engine (Optimized)")
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isRunning) ecsViewModel.engine.stopSimulation()
                    else ecsViewModel.engine.startSimulation()
                    isRunning = !isRunning
                },
                containerColor = if (isRunning) ErrorColor else Cyan400,
                contentColor = BgDark,
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = "Toggle Simulation")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Live Performance & Sovereignty Telemetry HUD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, Cyan400.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ENGINE FPS", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text("${telemetry.fps}", style = MaterialTheme.typography.titleMedium, color = Cyan400, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("TICK LATENCY", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text("${telemetry.tickDurationMs} ms", style = MaterialTheme.typography.titleMedium, color = if (telemetry.tickDurationMs > 16) ErrorColor else NeonGreen, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("ASTAROTH INDEX", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text("${(telemetry.astarothReliability * 100).toInt()}%", style = MaterialTheme.typography.titleMedium, color = Purple600, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("SOVEREIGNTY", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text("Lvl ${telemetry.sovereigntyLevel}", style = MaterialTheme.typography.titleMedium, color = GoldAccent, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visual Simulation Map
            Text("LIVE UNIVERSE MAP", style = MaterialTheme.typography.labelSmall, color = Cyan400)
            Spacer(modifier = Modifier.height(6.dp))
            com.example.ui.components.SimulationViewer(
                renderState = renderState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            // MetaBrain Log (The Emergence Engine)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark)
                    .border(1.dp, OutlineDark, RoundedCornerShape(20.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("METABRAIN LOG", style = MaterialTheme.typography.labelSmall, color = Cyan400)
                        Text("ENTITIES: ${telemetry.activeEntities}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyColumn(reverseLayout = false, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(engineLog) { log ->
                            Text("> $log", style = MaterialTheme.typography.bodyMedium, color = if (log.contains("META-EMERGENCE")) GoldAccent else TextSecondary)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text("POPULATION (${profiles.size})", style = MaterialTheme.typography.labelSmall, color = Cyan400)
            Spacer(modifier = Modifier.height(6.dp))
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                items(profiles) { profile ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceVariantDark)
                            .border(1.dp, OutlineDark, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(profile.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                                Text(profile.brain?.state?.name ?: "Unknown", style = MaterialTheme.typography.labelMedium, color = Purple600)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Current Goal: ${profile.brain?.currentGoal ?: "None"}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                val hunger = profile.needs?.hunger ?: 0f
                                val energy = profile.needs?.energy ?: 0f
                                Text("Hunger: ${hunger.toInt()}%", style = MaterialTheme.typography.labelSmall, color = if (hunger < 30) ErrorColor else TextSecondary)
                                Text("Energy: ${energy.toInt()}%", style = MaterialTheme.typography.labelSmall, color = if (energy < 30) ErrorColor else TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}
