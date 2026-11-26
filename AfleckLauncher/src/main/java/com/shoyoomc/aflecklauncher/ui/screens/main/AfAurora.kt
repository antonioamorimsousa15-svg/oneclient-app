package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Fundo Afleck Forja: preto profundo e grafite, com luz ambiente laranja e salmão bem discreta. Estático, sem custo por frame. */
@Composable
fun AfAuroraBackground(
    modifier: Modifier = Modifier,
    c1: Color = AfAccent,
    c2: Color = AfAccent2,
    animated: Boolean = false
) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        drawRect(
            Brush.verticalGradient(
                listOf(Color(0xFF0E0B0A), Color(0xFF080606), Color(0xFF050404)),
                startY = 0f,
                endY = h
            )
        )
        // luz ambiente quente no canto de cima à esquerda
        drawCircle(
            Brush.radialGradient(listOf(c1.copy(alpha = 0.16f), Color.Transparent), center = Offset(w * 0.10f, h * 0.02f), radius = w * 0.60f),
            radius = w * 0.60f, center = Offset(w * 0.10f, h * 0.02f)
        )
        // luz salmão no canto de baixo à direita
        drawCircle(
            Brush.radialGradient(listOf(c2.copy(alpha = 0.10f), Color.Transparent), center = Offset(w * 0.95f, h * 0.98f), radius = w * 0.50f),
            radius = w * 0.50f, center = Offset(w * 0.95f, h * 0.98f)
        )
        // fios de grafite: linhas horizontais bem finas, textura industrial
        var y = 0f
        while (y < h) {
            drawLine(Color.White.copy(alpha = 0.012f), Offset(0f, y), Offset(w, y), 1f)
            y += 6f * density
        }
    }
}
