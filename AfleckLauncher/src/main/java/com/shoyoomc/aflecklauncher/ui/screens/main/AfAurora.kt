package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

/**
 * Fundo aurora: preto profundo com cortinas de luz que se movem devagar, nas cores do tema.
 * Leve: redesenha a ~25 quadros por segundo e para por completo se a animação estiver desligada.
 */
@Composable
fun AfAuroraBackground(
    modifier: Modifier = Modifier,
    c1: Color = AfAccent,
    c2: Color = AfAccent2,
    animated: Boolean = true
) {
    val run = animated && AfThemeState.auroraAnim
    val time = remember { mutableFloatStateOf(0f) }
    if (run) {
        LaunchedEffect(Unit) {
            var last = 0L
            while (true) {
                withFrameMillis { ms ->
                    if (last == 0L) last = ms
                    time.floatValue = (time.floatValue + (ms - last) / 1000f) % 432f
                    last = ms
                }
                delay(40)
            }
        }
    }
    val two = (PI * 2).toFloat()
    val mid = lerp(c1, c2, 0.5f)

    Canvas(modifier.background(Color(0xFF010204))) {
        val w = size.width
        val h = size.height
        val pp = if (run) time.floatValue * two / 16f else 1.3f
        val qq = if (run) time.floatValue * two / 27f else 2.1f

        drawCircle(
            brush = Brush.radialGradient(
                listOf(c1.copy(alpha = 0.22f), Color.Transparent),
                center = Offset(w * (0.25f + 0.1f * sin(pp)), h * 0.85f),
                radius = w * 0.55f
            ),
            radius = w * 0.55f,
            center = Offset(w * (0.25f + 0.1f * sin(pp)), h * 0.85f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(c2.copy(alpha = 0.20f), Color.Transparent),
                center = Offset(w * (0.78f + 0.08f * sin(qq)), h * 0.8f),
                radius = w * 0.5f
            ),
            radius = w * 0.5f,
            center = Offset(w * (0.78f + 0.08f * sin(qq)), h * 0.8f)
        )

        fun band(phase: Float, yBase: Float, amp: Float, thick: Float, c: Color, alpha: Float, freq: Float) {
            val steps = 18
            val path = Path()
            for (i in 0..steps) {
                val x = w * i / steps
                val y = h * yBase + sin(x / w * freq * two + phase) * h * amp
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            for (i in steps downTo 0) {
                val x = w * i / steps
                val y = h * yBase + sin(x / w * freq * two + phase) * h * amp +
                    h * thick * (0.75f + 0.25f * sin(x / w * 5f + phase * 1.3f))
                path.lineTo(x, y)
            }
            path.close()
            drawPath(
                path,
                Brush.verticalGradient(
                    listOf(c.copy(alpha = 0f), c.copy(alpha = alpha), c.copy(alpha = 0f)),
                    startY = h * (yBase - amp),
                    endY = h * (yBase + amp + thick)
                )
            )
        }
        band(pp, 0.30f, 0.10f, 0.42f, c1, 0.50f, 0.9f)
        band(qq + 1.7f, 0.38f, 0.08f, 0.38f, c2, 0.42f, 1.3f)
        band(pp * 0.7f + qq, 0.25f, 0.07f, 0.34f, mid, 0.36f, 0.7f)
    }
}
