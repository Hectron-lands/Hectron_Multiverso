package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ecs.RenderState
import com.example.ui.theme.Cyan400
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.OutlineDark
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextSecondary

@Composable
fun SimulationViewer(renderState: RenderState?, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceDark)
            .border(1.dp, OutlineDark, RoundedCornerShape(24.dp))
    ) {
        if (renderState == null) return@Canvas
        
        val scale = size.width / 120f // Assume world size is ~100 with padding
        val offset = 10f * scale

        // Draw Roads
        renderState.roads.forEach { road ->
            val from = renderState.roadNodes[road.from] ?: return@forEach
            val to = renderState.roadNodes[road.to] ?: return@forEach
            
            drawLine(
                color = OutlineDark.copy(alpha = 0.5f),
                start = Offset(from.x * scale + offset, from.y * scale + offset),
                end = Offset(to.x * scale + offset, to.y * scale + offset),
                strokeWidth = 2f
            )
        }
        
        // Draw Entities
        renderState.entities.forEach { entity ->
            drawCircle(
                color = if (entity.isStressed) ErrorColor else if (entity.isEmployed) Cyan400 else TextSecondary,
                radius = 6f,
                center = Offset(entity.x * scale + offset, entity.y * scale + offset)
            )
        }
    }
}
