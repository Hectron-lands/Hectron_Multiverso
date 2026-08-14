package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun FactionsScreen(viewModel: UniverseViewModel) {
    val factions by viewModel.factions.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    
    Scaffold(
        containerColor = BgDark,
        topBar = {
            CustomHeader(universeName = "Factions & Achievements")
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Text("FACTIONS", color = Cyan400, style = MaterialTheme.typography.labelSmall)
            }
            items(factions) { faction ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(SurfaceDark)
                        .border(1.dp, OutlineDark, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(12.dp).background(Color(android.graphics.Color.parseColor(faction.colorHex)), CircleShape))
                            Text(faction.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(faction.description, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .background(SurfaceVariantDark, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Bonus: ${faction.bonus}", style = MaterialTheme.typography.labelMedium, color = GoldAccent)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("ACHIEVEMENTS", color = Cyan400, style = MaterialTheme.typography.labelSmall)
            }
            items(achievements) { achievement ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceDark)
                        .border(1.dp, OutlineDark, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(SurfaceVariantDark, CircleShape)
                            .border(1.dp, OutlineDark, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(achievement.iconName.first().toString(), color = Cyan400, style = MaterialTheme.typography.titleLarge)
                    }
                    Column {
                        Text(achievement.name, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(achievement.description, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }
        }
    }
}
