package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*

@Composable
fun LegalScreen() {
    Scaffold(
        containerColor = BgDark,
        topBar = {
            CustomHeader(universeName = "Legal & Compliance")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            LegalSection(
                title = "1. CORPORATE IDENTITY",
                content = "HECTRON Live Universe is a proprietary technology developed by Abadalabs, Inc. (Delaware C-Corp). All rights reserved 2026."
            )
            Spacer(modifier = Modifier.height(16.dp))
            LegalSection(
                title = "2. TERMS OF SERVICE",
                content = "By accessing the HECTRON Engine, you agree to comply with the Abadalabs Enterprise License Agreement. Reverse engineering of the MetaBrain System is strictly prohibited."
            )
            Spacer(modifier = Modifier.height(16.dp))
            LegalSection(
                title = "3. PRIVACY POLICY",
                content = "Abadalabs, Inc. complies with GDPR and CCPA. We do not sell your personal data. Telemetry collected from the Engine is used exclusively for AI training and streaming optimization."
            )
            Spacer(modifier = Modifier.height(16.dp))
            LegalSection(
                title = "4. ETHICAL AI USAGE",
                content = "The HECTRON MetaBrain operates under the Abadalabs Ethical AI Framework. All generated lore is subject to content filtering to ensure compliance with streaming platform guidelines."
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Abadalabs, Inc.\n131 Continental Dr, Suite 305\nNewark, DE 19713",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun LegalSection(title: String, content: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, OutlineDark, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleSmall, color = Cyan400, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(content, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
        }
    }
}
