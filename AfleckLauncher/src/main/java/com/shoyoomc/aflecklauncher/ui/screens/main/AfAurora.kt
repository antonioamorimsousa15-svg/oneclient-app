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
        // grade hexagonal discreta (textura industrial), um único traçado estático
        val r = 36f * density
        val hw = r * 1.5f
        val hh = r * 1.7320508f
        val path = androidx.compose.ui.graphics.Path()
        var col = 0
        var cx = 0f
        while (cx < w + r) {
            var cy = -hh + (if (col % 2 == 0) 0f else hh / 2f)
            while (cy < h + hh) {
                for (k in 0..5) {
                    val ang = Math.toRadians(60.0 * k).toFloat()
                    val px = cx + r * kotlin.math.cos(ang)
                    val py = cy + r * kotlin.math.sin(ang)
                    if (k == 0) path.moveTo(px, py) else path.lineTo(px, py)
                }
                path.close()
                cy += hh
            }
            cx += hw
            col++
        }
        drawPath(path, c1.copy(alpha = 0.06f), style = androidx.compose.ui.graphics.drawscope.Stroke(1f * density))
    }
}
