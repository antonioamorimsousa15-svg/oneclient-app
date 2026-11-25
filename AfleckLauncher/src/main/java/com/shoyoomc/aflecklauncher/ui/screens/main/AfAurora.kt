package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Fundo espacial estático Afleck: azul-noite, névoa azul discreta e estrelas. Sem loop de animação. */
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
                listOf(Color(0xFF061126), Color(0xFF0B1D3B), Color(0xFF030914)),
                startY = 0f,
                endY = h
            )
        )
        drawCircle(
            Brush.radialGradient(listOf(Color(0x553B75C8), Color(0x003B75C8)),
                center = Offset(w * 0.48f, h * 0.22f), radius = w * 0.72f),
            radius = w * 0.72f, center = Offset(w * 0.48f, h * 0.22f)
        )
        drawCircle(
            Brush.radialGradient(listOf(c1.copy(alpha = 0.10f), Color.Transparent),
                center = Offset(w * 0.86f, h * 0.78f), radius = w * 0.48f),
            radius = w * 0.48f, center = Offset(w * 0.86f, h * 0.78f)
        )
        // Coordenadas determinísticas: estrelas estáticas, sem custo de animação por frame.
        for (i in 0 until 96) {
            val x = ((i * 73 + 19) % 997) / 997f * w
            val y = ((i * 137 + 41) % 991) / 991f * h
            val r = if (i % 11 == 0) 1.7f else if (i % 4 == 0) 1.1f else 0.65f
            val a = if (i % 7 == 0) 0.78f else 0.42f
            drawCircle(Color.White.copy(alpha = a), r, Offset(x, y))
        }
        // Horizonte azul frio, bem sutil, para dar profundidade sem competir com os cartões.
        drawRect(
            Brush.verticalGradient(
                0.0f to Color.Transparent,
                0.72f to Color.Transparent,
                1.0f to Color(0x331D4B78),
                startY = 0f,
                endY = h
            )
        )
    }
}
