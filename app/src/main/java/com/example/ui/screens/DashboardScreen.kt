package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodels.UniverseViewModel

@Composable
fun DashboardScreen(
    viewModel: UniverseViewModel,
    onNavigateToSubscriptions: () -> Unit,
    onNavigateToLegal: () -> Unit
) {
    val universe by viewModel.universe.collectAsState()
    val planets by viewModel.planets.collectAsState()
    val events by viewModel.events.collectAsState()
    val players by viewModel.players.collectAsState()

    LaunchedEffect(universe) {
        if (universe == null) {
            viewModel.initializeUniverse("Hectron Prime", "Hectron")
        }
    }

    Scaffold(
        containerColor = BgDark,
        topBar = {
            CustomHeader(universeName = universe?.name ?: "Live Universe")
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                HeroCard(
                    planetName = planets.firstOrNull()?.name ?: "Unknown Target",
                    planetType = planets.firstOrNull()?.type ?: "Unclassified",
                    playersCount = players.size
                )
            }

            item {
                StatsRow(
                    iron = planets.sumOf { it.iron },
                    gold = planets.sumOf { it.gold },
                    energy = planets.sumOf { it.energy }
                )
            }

            item {
                LiveFeed(events = events.take(5))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToSubscriptions,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Purple600),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("UPGRADE", color = Purple600, style = MaterialTheme.typography.labelMedium)
                    }
                    OutlinedButton(
                        onClick = onNavigateToLegal,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, OutlineDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("LEGAL", color = TextTertiary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomHeader(universeName: String) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "HECTRON PORTAL V1.0",
                color = Cyan400,
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = universeName,
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color.Red.copy(alpha = alpha), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF0891B2), Color(0xFF9333EA))
                        ),
                        shape = CircleShape
                    )
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("H", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HeroCard(planetName: String, planetType: String, playersCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(SurfaceDark)
            .border(1.dp, OutlineDark, RoundedCornerShape(40.dp))
    ) {
        // Subtle background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.radialGradient(listOf(Color(0xFF1E1B4B), Color.Transparent)))
        )

        // Center Planet representation
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(140.dp)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFFFB923C), Color(0xFFDC2626), Color(0xFF581C87))
                    ),
                    shape = CircleShape
                )
                .blur(2.dp)
        )

        // Watchers badge
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(24.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .border(1.dp, OutlineDark, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFF4ADE80), CircleShape)
            )
            Text(
                text = "${playersCount * 10} Watchers",
                color = TextPrimary,
                style = MaterialTheme.typography.labelSmall
            )
        }

        // Info panel
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .border(1.dp, OutlineDark, RoundedCornerShape(24.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text("CURRENT TARGET", color = Cyan400, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(planetName, color = TextPrimary, style = MaterialTheme.typography.titleMedium)
                Text("$planetType Class • Sector 09", color = TextSecondary, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Normal)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("AI Narrative", color = TextTertiary, style = MaterialTheme.typography.labelSmall, fontStyle = FontStyle.Italic)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "\"Rivers of liquid gold flow...\"",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelMedium,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.widthIn(max = 100.dp)
                )
            }
        }
    }
}

@Composable
fun StatsRow(iron: Int, gold: Int, energy: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(modifier = Modifier.weight(1f), label = "Iron", value = iron.toString(), color = TextSecondary)
        StatCard(modifier = Modifier.weight(1f), label = "Gold", value = gold.toString(), color = GoldAccent)
        StatCard(modifier = Modifier.weight(1f), label = "Energy", value = energy.toString(), color = Cyan400)
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, value: String, color: Color) {
    Column(
        modifier = modifier
            .background(SurfaceVariantDark, RoundedCornerShape(24.dp))
            .border(1.dp, OutlineDark, RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = color, style = MaterialTheme.typography.labelSmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LiveFeed(events: List<com.example.data.UniverseEvent>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariantDark, RoundedCornerShape(24.dp))
            .border(1.dp, OutlineDark, RoundedCornerShape(24.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("LIVE FEED", color = TextTertiary, style = MaterialTheme.typography.labelSmall)
            HorizontalDivider(modifier = Modifier.weight(1f), color = OutlineDark)
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        if (events.isEmpty()) {
            Text("No recent events.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                events.forEach { event ->
                    Text(
                        text = buildString {
                            append(event.title)
                            append(": ")
                            append(event.description)
                        },
                        color = TextPrimary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Normal,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
