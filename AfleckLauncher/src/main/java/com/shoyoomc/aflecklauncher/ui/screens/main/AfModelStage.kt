package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.cos

private const val NODES = 48

/**
 * Palco animado do HUD: fundo preto com uma rede de pontos e linhas que se move devagar,
 * mais densa e brilhante no centro. Usa a cor de destaque do tema (muda junto com ele).
 */
@Composable
fun AfModelStage(modifier: Modifier = Modifier, content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit) {
    var time by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            androidx.compose.runtime.withFrameNanos { time = it / 1_000_000_000f }
        }
    }
    val accent = AfAccent
    val border = AfBorder
    // pontos fixos (base + fase), gerados uma vez
    val base = remember {
        FloatArray(NODES * 5).also { a ->
            for (i in 0 until NODES) {
                val s1 = ((i * 7919) % 1000) / 1000f
                val s2 = ((i * 104729) % 1000) / 1000f
                val s3 = ((i * 15485863L) % 1000L) / 1000f
                val hub = i < 16
                a[i * 5] = if (hub) 0.5f + (s1 - 0.5f) * 0.22f else s1
                a[i * 5 + 1] = if (hub) 0.5f + (s2 - 0.5f) * 0.22f else s2
                a[i * 5 + 2] = s3 * 6.28f
                a[i * 5 + 3] = 0.25f + s2 * 0.45f
                a[i * 5 + 4] = if (hub) 0.03f else 0.05f + s1 * 0.05f
            }
        }
    }
    Box(modifier) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(lerp(Color.Black, accent, 0.07f).copy(alpha = 0.22f))
            drawHudGrid(accent)
            // brilho do centro
            drawCircle(
                Brush.radialGradient(
                    listOf(accent.copy(alpha = 0.30f), Color.Transparent),
                    center = Offset(w / 2f, h / 2f), radius = minOf(w, h) * 0.55f
                ),
                radius = minOf(w, h) * 0.55f, center = Offset(w / 2f, h / 2f)
            )
            val px = FloatArray(NODES)
            val py = FloatArray(NODES)
            for (i in 0 until NODES) {
                val ph = base[i * 5 + 2]
                val sp = base[i * 5 + 3]
                val amp = base[i * 5 + 4]
                px[i] = (base[i * 5] + amp * sin(time * sp + ph)) * w
                py[i] = (base[i * 5 + 1] + amp * cos(time * sp * 0.8f + ph)) * h
            }
            val maxD = minOf(w, h) * 0.38f
            val lw = 0.8.dp.toPx()
            for (i in 0 until NODES) {
                for (j in i + 1 until NODES) {
                    val d = hypot(px[i] - px[j], py[i] - py[j])
                    if (d < maxD) {
                        val k = 1f - d / maxD
                        drawLine(accent.copy(alpha = k * k * 0.55f), Offset(px[i], py[i]), Offset(px[j], py[j]), lw)
                    }
                }
            }
            for (i in 0 until NODES) {
                val tw = 0.55f + 0.45f * sin(time * 1.6f + base[i * 5 + 2])
                drawCircle(Color.White.copy(alpha = 0.85f * tw), radius = 1.3.dp.toPx(), center = Offset(px[i], py[i]))
            }
            drawHudBars(accent)
            drawHudFrame(accent)
        }
        content()
    }
}


private fun DrawScope.drawHudGrid(accent: Color) {
    val step = 24.dp.toPx()
    val c = accent.copy(alpha = 0.07f)
    var x = 0f
    while (x < size.width) { drawLine(c, Offset(x, 0f), Offset(x, size.height), 1f); x += step }
    var y = 0f
    while (y < size.height) { drawLine(c, Offset(0f, y), Offset(size.width, y), 1f); y += step }
}

/** Barras técnicas de cima e de baixo (espelhadas), com trechos grossos e brilho. */
private fun DrawScope.drawHudBars(accent: Color) {
    val w = size.width
    val h = size.height
    val u = 1.dp.toPx()
    fun bar(flip: Boolean) {
        fun Y(v: Float) = if (flip) h - v else v
        val y0 = 6 * u
        val pts = listOf(
            0f to y0 + 8 * u, 0.16f * w to y0 + 8 * u, 0.20f * w to y0 + 2 * u, 0.36f * w to y0 + 2 * u,
            0.40f * w to y0 + 12 * u, 0.60f * w to y0 + 12 * u, 0.64f * w to y0 + 2 * u,
            0.80f * w to y0 + 2 * u, 0.84f * w to y0 + 8 * u, w to y0 + 8 * u
        )
        val p = Path().apply {
            pts.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(x, Y(y)) else lineTo(x, Y(y)) }
        }
        drawPath(p, accent.copy(alpha = 0.25f), style = Stroke(5 * u))
        drawPath(p, accent, style = Stroke(1.6f * u))
        // linha interna mais fraca
        val q = Path().apply {
            pts.forEachIndexed { i, (x, y) -> if (i == 0) moveTo(x, Y(y + 6 * u)) else lineTo(x, Y(y + 6 * u)) }
        }
        drawPath(q, accent.copy(alpha = 0.35f), style = Stroke(1f * u))
        // trechos grossos nas laterais
        for ((x0, x1) in listOf(0.02f to 0.12f, 0.88f to 0.98f)) {
            drawLine(accent.copy(alpha = 0.3f), Offset(x0 * w, Y(y0 + 8 * u)), Offset(x1 * w, Y(y0 + 8 * u)), 7 * u, StrokeCap.Round)
            drawLine(accent, Offset(x0 * w, Y(y0 + 8 * u)), Offset(x1 * w, Y(y0 + 8 * u)), 3 * u, StrokeCap.Round)
        }
        // pontinhos / tracejado
        for (i in 0 until 4) {
            val x = 0.05f * w + i * 8 * u
            drawLine(accent.copy(alpha = 0.7f), Offset(x, Y(y0 + 18 * u)), Offset(x + 4 * u, Y(y0 + 18 * u)), 1.5f * u)
            val x2 = 0.95f * w - i * 8 * u
            drawLine(accent.copy(alpha = 0.7f), Offset(x2, Y(y0 + 18 * u)), Offset(x2 - 4 * u, Y(y0 + 18 * u)), 1.5f * u)
        }
    }
    bar(false)
    bar(true)
}

/** Moldura central com cantos cortados, entalhes laterais, setas e marcadores. */
private fun DrawScope.drawHudFrame(accent: Color) {
    val w = size.width
    val h = size.height
    val u = 1.dp.toPx()
    val l = 0.10f * w
    val r = 0.90f * w
    val t = 0.20f * h
    val b = 0.80f * h
    val c = 12 * u
    val fw = r - l
    val fh = b - t
    val p = Path().apply {
        moveTo(l + c, t)
        lineTo(l + 0.30f * fw, t); lineTo(l + 0.30f * fw + 6 * u, t + 5 * u)
        lineTo(l + 0.70f * fw - 6 * u, t + 5 * u); lineTo(l + 0.70f * fw, t)
        lineTo(r - c, t); lineTo(r, t + c)
        lineTo(r, t + 0.42f * fh); lineTo(r - 5 * u, t + 0.42f * fh + 5 * u)
        lineTo(r - 5 * u, t + 0.58f * fh - 5 * u); lineTo(r, t + 0.58f * fh)
        lineTo(r, b - c); lineTo(r - c, b)
        lineTo(l + c, b); lineTo(l, b - c)
        lineTo(l, t + 0.58f * fh); lineTo(l + 5 * u, t + 0.58f * fh - 5 * u)
        lineTo(l + 5 * u, t + 0.42f * fh + 5 * u); lineTo(l, t + 0.42f * fh)
        lineTo(l, t + c)
        close()
    }
    drawPath(p, accent.copy(alpha = 0.05f))
    drawPath(p, accent.copy(alpha = 0.25f), style = Stroke(6 * u))
    drawPath(p, accent, style = Stroke(1.6f * u))
    // contorno interno fraco
    val inset = 7 * u
    drawRect(accent.copy(alpha = 0.18f), Offset(l + inset, t + inset), androidx.compose.ui.geometry.Size(fw - 2 * inset, fh - 2 * inset), style = Stroke(1f * u))

    // setas ‹‹‹ e ››› acima da moldura
    val cy = t - 16 * u
    for (i in 0 until 3) {
        val x = l + 10 * u + i * 7 * u
        drawPath(Path().apply { moveTo(x + 4 * u, cy - 4 * u); lineTo(x, cy); lineTo(x + 4 * u, cy + 4 * u) }, accent.copy(alpha = 0.9f), style = Stroke(1.5f * u, cap = StrokeCap.Round))
        val x2 = r - 10 * u - i * 7 * u
        drawPath(Path().apply { moveTo(x2 - 4 * u, cy - 4 * u); lineTo(x2, cy); lineTo(x2 - 4 * u, cy + 4 * u) }, accent.copy(alpha = 0.9f), style = Stroke(1.5f * u, cap = StrokeCap.Round))
    }
    // grupos de 3 pontos nos cantos
    for (i in 0 until 3) {
        drawCircle(accent, 1.6f * u, Offset(0.04f * w + i * 6 * u, t - 16 * u))
        drawCircle(accent, 1.6f * u, Offset(0.96f * w - i * 6 * u, t - 16 * u))
    }
    // setas para cima embaixo
    val ay = b + 16 * u
    for (f in listOf(0.14f, 0.5f, 0.86f)) {
        val x = l + fw * f
        drawPath(Path().apply { moveTo(x - 6 * u, ay + 4 * u); lineTo(x, ay - 4 * u); lineTo(x + 6 * u, ay + 4 * u); close() }, accent)
    }
}
