package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Fundo "Dark Futuristic Gaming": azul-marinho quase preto, brilho ciano nos cantos, grade técnica, traços de circuito, feixes diagonais e linhas de varredura. Estático (um único desenho). */
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
        val d = density
        drawRect(
            Brush.verticalGradient(
                listOf(Color(0xFF030A1A), Color(0xFF020712), Color(0xFF01040C)),
                startY = 0f,
                endY = h
            )
        )
        drawCircle(
            Brush.radialGradient(listOf(c1.copy(alpha = 0.22f), Color.Transparent), center = Offset(w * 0.08f, h * 0.0f), radius = w * 0.55f),
            radius = w * 0.55f, center = Offset(w * 0.08f, h * 0.0f)
        )
        drawCircle(
            Brush.radialGradient(listOf(c2.copy(alpha = 0.16f), Color.Transparent), center = Offset(w * 0.98f, h * 1.0f), radius = w * 0.50f),
            radius = w * 0.50f, center = Offset(w * 0.98f, h * 1.0f)
        )
        drawCircle(
            Brush.radialGradient(listOf(Color(0xFF3A5BFF).copy(alpha = 0.10f), Color.Transparent), center = Offset(w * 0.55f, h * 0.55f), radius = w * 0.40f),
            radius = w * 0.40f, center = Offset(w * 0.55f, h * 0.55f)
        )
        // grade técnica
        val step = 44f * d
        val grid = androidx.compose.ui.graphics.Path()
        var gx = 0f
        while (gx <= w) { grid.moveTo(gx, 0f); grid.lineTo(gx, h); gx += step }
        var gy = 0f
        while (gy <= h) { grid.moveTo(0f, gy); grid.lineTo(w, gy); gy += step }
        drawPath(grid, c1.copy(alpha = 0.045f), style = androidx.compose.ui.graphics.drawscope.Stroke(1f * d))
        // feixes diagonais
        val beams = androidx.compose.ui.graphics.Path()
        for (k in 0..5) {
            val x0 = w * (0.10f + k * 0.17f)
            beams.moveTo(x0, h); beams.lineTo(x0 + h * 0.55f, 0f)
        }
        drawPath(beams, c1.copy(alpha = 0.035f), style = androidx.compose.ui.graphics.drawscope.Stroke(2f * d))
        // traços de circuito (retas com cantos de 45 graus)
        val trace = androidx.compose.ui.graphics.Path()
        fun circuit(x: Float, y: Float, run: Float, drop: Float, run2: Float) {
            trace.moveTo(x, y); trace.lineTo(x + run, y); trace.lineTo(x + run + drop, y + drop); trace.lineTo(x + run + drop + run2, y + drop)
        }
        circuit(w * 0.02f, h * 0.88f, w * 0.10f, 26f * d, w * 0.14f)
        circuit(w * 0.60f, h * 0.06f, w * 0.12f, 30f * d, w * 0.20f)
        circuit(w * 0.30f, h * 0.96f, w * 0.18f, -24f * d, w * 0.16f)
        circuit(w * 0.70f, h * 0.78f, w * 0.08f, -28f * d, w * 0.18f)
        drawPath(trace, c1.copy(alpha = 0.22f), style = androidx.compose.ui.graphics.drawscope.Stroke(1.2f * d))
        // nós nos circuitos
        listOf(Offset(w * 0.02f, h * 0.88f), Offset(w * 0.60f, h * 0.06f), Offset(w * 0.30f, h * 0.96f), Offset(w * 0.70f, h * 0.78f)).forEach {
            drawCircle(c1.copy(alpha = 0.5f), radius = 2.5f * d, center = it)
        }
        // linhas de varredura finas
        val scan = androidx.compose.ui.graphics.Path()
        var sy = 0f
        while (sy <= h) { scan.moveTo(0f, sy); scan.lineTo(w, sy); sy += 4f * d }
        drawPath(scan, Color.Black.copy(alpha = 0.18f), style = androidx.compose.ui.graphics.drawscope.Stroke(1f * d))
        // vinheta
        drawRect(Brush.radialGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)), center = Offset(w / 2f, h / 2f), radius = maxOf(w, h) * 0.75f))
    }
}
