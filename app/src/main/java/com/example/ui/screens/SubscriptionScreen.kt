package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SubscriptionScreen() {
    Scaffold(
        containerColor = BgDark,
        topBar = {
            CustomHeader(universeName = "Enterprise Plans")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("ELEVATE YOUR UNIVERSE", style = MaterialTheme.typography.titleLarge, color = TextPrimary, fontWeight = FontWeight.ExtraBold)
            Text("Select a plan to unlock the full potential of Abadalabs AI.", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            SubscriptionCard(
                title = "HECTRON BASIC",
                price = "FREE",
                description = "For hobbyists and explorers.",
                features = listOf("Standard AI Simulation", "Local Lore Generation", "Standard Pathfinding"),
                color = TextTertiary,
                isSelected = false
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SubscriptionCard(
                title = "HECTRON PRO",
                price = "$19.99 / mo",
                description = "For professional streamers.",
                features = listOf("Gemini 1.5 Pro Access", "OBS Direct Integration", "Custom Voice (11Labs)", "BigQuery Lore Storage"),
                color = Cyan400,
                isSelected = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SubscriptionCard(
                title = "ENTERPRISE",
                price = "$99.99 / mo",
                description = "For Abadalabs partners.",
                features = listOf("Unlimited Tokens", "Multi-Universe Sync", "Dedicated Support", "SLA Guarantee", "Stripe Connect Integration"),
                color = Purple600,
                isSelected = false
            )
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun SubscriptionCard(
    title: String,
    price: String,
    description: String,
    features: List<String>,
    color: androidx.compose.ui.graphics.Color,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(if (isSelected) SurfaceDark else SurfaceVariantDark)
            .border(if (isSelected) 2.dp else 1.dp, if (isSelected) color else OutlineDark, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
                if (isSelected) {
                    Surface(color = color, shape = CircleShape) {
                        Text("ACTIVE", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = BgDark)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(price, style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.ExtraBold)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = OutlineDark)
            Spacer(modifier = Modifier.height(16.dp))
            
            features.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feature, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* Integrate Stripe */ },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = BgDark),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (isSelected) "MANAGE SUBSCRIPTION" else "UPGRADE NOW")
            }
        }
    }
}
