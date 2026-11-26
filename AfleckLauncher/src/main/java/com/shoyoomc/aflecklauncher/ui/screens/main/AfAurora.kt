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
                listOf(Color(0xFF0D0706), Color(0xFF080504), Color(0xFF040302)),
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
        // grade quadrada discreta (textura de painel técnico), um único traçado estático
        val step = 55f * density
        val path = androidx.compose.ui.graphics.Path()
        var gx = 0f
        while (gx <= w) { path.moveTo(gx, 0f); path.lineTo(gx, h); gx += step }
        var gy = 0f
        while (gy <= h) { path.moveTo(0f, gy); path.lineTo(w, gy); gy += step }
        drawPath(path, c1.copy(alpha = 0.05f), style = androidx.compose.ui.graphics.drawscope.Stroke(1f * density))
    }
}
