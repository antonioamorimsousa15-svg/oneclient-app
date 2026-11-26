package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Palco da skin: fundo escuro limpo e, embaixo do personagem, uma plataforma holográfica
 * de anéis que giram em círculos (arcos, pontos e marcas), com feixe de luz subindo.
 * Usa a cor de destaque do tema.
 */
@Composable
fun AfModelStage(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            androidx.compose.runtime.withFrameNanos { time = it / 1_000_000_000f }
        }
    }
    val accent = AfAccent
    val accent2 = AfAccent2
    Box(modifier) {
        Canvas(Modifier.fillMaxSize()) { drawHoloPlatform(time, accent, accent2) }
        content()
    }
}

private fun DrawScope.drawHoloPlatform(t: Float, accent: Color, accent2: Color) {
    val w = size.width
    val h = size.height
    val k = 0.30f                       // achatamento (perspectiva do chão)
    val cx = w / 2f
    val cy = h * 0.76f
    val R = minOf(w * 0.44f, h * 0.50f)
    val u = 1.dp.toPx()
    val hot = Color(0xFFFFE2B8)

    drawRect(Color.Black.copy(alpha = 0.40f))

    // feixe de luz subindo da plataforma
    val beam = Path().apply {
        moveTo(cx - R * 0.55f, cy); lineTo(cx - R * 0.28f, h * 0.02f)
        lineTo(cx + R * 0.28f, h * 0.02f); lineTo(cx + R * 0.55f, cy); close()
    }
    drawPath(
        beam,
        Brush.verticalGradient(
            listOf(Color.Transparent, accent.copy(alpha = 0.07f), accent.copy(alpha = 0.20f)),
            startY = h * 0.02f, endY = cy
        )
    )

    // brilho no chão (círculo achatado)
    withTransform({
        translate(cx, cy)
        scale(1f, k, pivot = Offset.Zero)
    }) {
        drawCircle(
            Brush.radialGradient(listOf(accent.copy(alpha = 0.34f), accent.copy(alpha = 0.10f), Color.Transparent), center = Offset.Zero, radius = R * 1.2f),
            radius = R * 1.2f, center = Offset.Zero
        )
    }

    fun tl(r: Float, dy: Float = 0f) = Offset(cx - r, cy - r * k + dy)
    fun sz(r: Float) = Size(2f * r, 2f * r * k)
    fun px(r: Float, deg: Float) = cx + r * cos(Math.toRadians(deg.toDouble())).toFloat()
    fun py(r: Float, deg: Float) = cy + r * k * sin(Math.toRadians(deg.toDouble())).toFloat()

    // espessura da plataforma (anel de baixo, mais fraco)
    drawArc(accent.copy(alpha = 0.22f), 0f, 360f, false, tl(R, 7 * u), sz(R), style = Stroke(1.4f * u))
    // anel externo fino
    drawArc(accent.copy(alpha = 0.60f), 0f, 360f, false, tl(R), sz(R), style = Stroke(1.4f * u))

    // marcas (régua) girando devagar
    val rot0 = t * 12f
    for (i in 0 until 72) {
        val a = i * 5f + rot0
        val major = i % 6 == 0
        val r1 = R * 1.04f
        val r2 = if (major) R * 1.16f else R * 1.09f
        drawLine(
            if (major) hot.copy(alpha = 0.9f) else accent.copy(alpha = 0.5f),
            Offset(px(r1, a), py(r1, a)), Offset(px(r2, a), py(r2, a)),
            if (major) 1.8f * u else 1f * u
        )
    }

    // anel B: três arcos grossos girando no sentido anti-horário
    val rb = R * 0.84f
    val ang = -t * 55f
    for (i in 0 until 3) {
        val s = ang + i * 120f
        drawArc(accent.copy(alpha = 0.28f), s, 72f, false, tl(rb), sz(rb), style = Stroke(9 * u, cap = StrokeCap.Round))
        drawArc(accent, s, 72f, false, tl(rb), sz(rb), style = Stroke(3.2f * u, cap = StrokeCap.Round))
    }

    // anel C: pontos girando no sentido horário
    val rc = R * 0.66f
    for (i in 0 until 28) {
        val a = i * (360f / 28f) + t * 28f
        val big = i % 7 == 0
        drawCircle(
            if (big) hot else accent.copy(alpha = 0.75f),
            radius = (if (big) 2.4f else 1.4f) * u, center = Offset(px(rc, a), py(rc, a))
        )
    }

    // anel D: duplo, com dois arcos brilhantes rápidos
    val rd = R * 0.48f
    drawArc(accent.copy(alpha = 0.45f), 0f, 360f, false, tl(rd), sz(rd), style = Stroke(1.2f * u))
    drawArc(accent.copy(alpha = 0.30f), 0f, 360f, false, tl(rd * 0.9f), sz(rd * 0.9f), style = Stroke(1f * u))
    val fast = t * 110f
    for (i in 0 until 2) {
        val s = fast + i * 180f
        drawArc(hot.copy(alpha = 0.35f), s, 46f, false, tl(rd), sz(rd), style = Stroke(6 * u, cap = StrokeCap.Round))
        drawArc(hot, s, 46f, false, tl(rd), sz(rd), style = Stroke(2.2f * u, cap = StrokeCap.Round))
    }

    // anel E: núcleo
    val re = R * 0.26f
    drawArc(accent.copy(alpha = 0.18f), 0f, 360f, true, tl(re), sz(re))
    drawArc(accent, 0f, 360f, false, tl(re), sz(re), style = Stroke(1.6f * u))
    val pulse = 0.5f + 0.5f * sin(t * 2.4f)
    drawArc(hot.copy(alpha = 0.25f + 0.35f * pulse), 0f, 360f, false, tl(re * 0.55f), sz(re * 0.55f), style = Stroke(1.2f * u))

    // cantoneiras: quatro chaves em volta, fixas
    for (i in 0 until 4) {
        val s = 45f + i * 90f - 9f
        drawArc(accent2.copy(alpha = 0.85f), s, 18f, false, tl(R * 0.97f), sz(R * 0.97f), style = Stroke(2.6f * u, cap = StrokeCap.Round))
    }
}
