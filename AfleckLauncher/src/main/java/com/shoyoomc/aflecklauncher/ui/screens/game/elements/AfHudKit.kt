/*
 * AFLECK LAUNCHER — kit visual "tecnológico" do HUD de jogo.
 * Visual próprio do Afleck (cantoneiras, régua de marcação, células com brilho,
 * cabeçalho técnico e indicador de sinal animado). Baseado no ZalithLauncher 2 (GPLv3).
 */
package com.shoyoomc.aflecklauncher.ui.screens.game.elements

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoyoomc.aflecklauncher.ui.screens.main.AfAccent
import com.shoyoomc.aflecklauncher.ui.screens.main.AfBorder
import com.shoyoomc.aflecklauncher.ui.screens.main.AfCondensed
import com.shoyoomc.aflecklauncher.ui.screens.main.AfDim
import com.shoyoomc.aflecklauncher.ui.screens.main.AfText
import com.shoyoomc.aflecklauncher.ui.screens.main.afClick

/** Cantoneiras em "L" nos quatro cantos (cor do tema). */
internal fun Modifier.afTechCorners(len: Dp = 14.dp, inset: Dp = 3.dp, color: Color? = null): Modifier = this.drawBehind {
    val c = color ?: AfAccent
    val l = len.toPx()
    val i = inset.toPx()
    val w = size.width
    val h = size.height
    val sw = 1.6f.dp.toPx()
    drawLine(c, Offset(i, i), Offset(i + l, i), sw)
    drawLine(c, Offset(i, i), Offset(i, i + l), sw)
    drawLine(c, Offset(w - i, i), Offset(w - i - l, i), sw)
    drawLine(c, Offset(w - i, i), Offset(w - i, i + l), sw)
    drawLine(c, Offset(i, h - i), Offset(i + l, h - i), sw)
    drawLine(c, Offset(i, h - i), Offset(i, h - i - l), sw)
    drawLine(c, Offset(w - i, h - i), Offset(w - i - l, h - i), sw)
    drawLine(c, Offset(w - i, h - i), Offset(w - i, h - i - l), sw)
}

/** Régua de marcações no rodapé (traços curtos e longos), como instrumento de precisão. */
internal fun Modifier.afTechRuler(): Modifier = this.drawBehind {
    val step = 9.dp.toPx()
    val stroke = 1.dp.toPx()
    var x = step * 2f
    var n = 0
    while (x < size.width - step * 2f) {
        val long = n % 5 == 0
        val th = if (long) 5.dp.toPx() else 2.5f.dp.toPx()
        drawLine(
            AfAccent.copy(alpha = if (long) 0.45f else 0.22f),
            Offset(x, size.height),
            Offset(x, size.height - th),
            stroke
        )
        x += step
        n++
    }
}

/** Indicador de sinal animado (5 barras que sobem e descem). */
@Composable
internal fun AfSignal(modifier: Modifier = Modifier) {
    val t = rememberInfiniteTransition(label = "afSignal")
    val p by t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = LinearEasing)),
        label = "afSignalPhase"
    )
    Canvas(modifier.size(width = 28.dp, height = 16.dp)) {
        val bw = size.width / 9f
        for (i in 0 until 5) {
            val ph = (p + i * 0.17f) % 1f
            val tri = if (ph < 0.5f) ph * 2f else 2f - ph * 2f
            val bh = size.height * (0.25f + 0.75f * tri)
            drawRect(
                AfAccent.copy(alpha = 0.85f),
                Offset(i * 2f * bw, size.height - bh),
                Size(bw, bh)
            )
        }
    }
}

/** Célula de ícone do dock: retângulo de 5 dp, cantoneiras, brilho pulsante quando ativa. */
@Composable
internal fun AfTechCell(
    icon: Int,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val t = rememberInfiniteTransition(label = "afCell")
    val glow by t.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing), RepeatMode.Reverse),
        label = "afCellGlow"
    )
    val shape = RoundedCornerShape(5.dp)
    Column(
        modifier = modifier.afClick(onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 64.dp, height = 52.dp)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        if (active) listOf(AfAccent.copy(alpha = 0.30f), AfAccent.copy(alpha = 0.06f))
                        else listOf(Color.White.copy(alpha = 0.07f), Color.Black.copy(alpha = 0.45f))
                    )
                )
                .border(1.dp, if (active) AfAccent.copy(alpha = glow) else AfBorder, shape)
                .afTechCorners(len = 7.dp, inset = 2.dp, color = if (active) AfAccent else AfDim)
                .drawBehind {
                    if (active) {
                        drawRect(
                            AfAccent.copy(alpha = glow),
                            Offset(size.width * 0.2f, size.height - 3.dp.toPx()),
                            Size(size.width * 0.6f, 2.dp.toPx())
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = if (active) AfAccent else AfText,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = label.uppercase(),
            color = if (active) AfAccent else AfDim,
            fontSize = 11.sp,
            fontFamily = AfCondensed,
            letterSpacing = 0.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 3.dp)
        )
    }
}

/** Cabeçalho técnico dos painéis: código do módulo, título, sinal, fechar e régua. */
@Composable
internal fun AfTechHeader(code: String, title: String, onClose: () -> Unit) {
    val shape = RoundedCornerShape(5.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(shape)
                .background(AfAccent.copy(alpha = 0.14f))
                .border(1.dp, AfAccent, shape)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = code,
                color = AfAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AfCondensed,
                letterSpacing = 1.5.sp
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "AFLECK // MÓDULO",
                color = AfDim,
                fontSize = 9.sp,
                fontFamily = AfCondensed,
                letterSpacing = 2.sp
            )
            Text(
                text = title,
                color = AfText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AfCondensed,
                letterSpacing = 2.sp
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(shape)
                .border(1.dp, AfBorder, shape)
                .afClick(onClose),
            contentAlignment = Alignment.Center
        ) {
            Text("✕", color = AfText, fontSize = 14.sp)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .padding(horizontal = 12.dp)
            .drawBehind {
                drawLine(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, AfAccent, AfAccent.copy(alpha = 0.3f), Color.Transparent)
                    ),
                    Offset(0f, size.height - 1f),
                    Offset(size.width, size.height - 1f),
                    1.5f.dp.toPx()
                )
            }
            .afTechRuler()
    )
}

/** Faixa de dica: explica em uma frase para que serve o painel aberto. */
@Composable
internal fun AfTechHint(text: String) {
    val shape = RoundedCornerShape(5.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.35f))
            .border(1.dp, AfBorder, shape)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(AfAccent.copy(alpha = 0.2f))
                .border(1.dp, AfAccent, RoundedCornerShape(5.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("?", color = AfAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            text = text,
            color = AfText,
            fontSize = 13.sp,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Movimento do HUD: linha na cor do tema que sobe e desce no fundo e setas animadas
 * (">>>" e "<<<") nas laterais, acendendo em sequência.
 */
internal fun Modifier.afTechMotion(arrows: Boolean = true, scan: Boolean = true): Modifier = composed {
    val t = rememberInfiniteTransition(label = "afMotion")
    val ph by t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3200, easing = LinearEasing), RepeatMode.Reverse),
        label = "afScanPhase"
    )
    val wv by t.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing)),
        label = "afArrowWave"
    )
    Modifier.drawBehind {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@drawBehind
        val ac = AfAccent
        if (scan) {
            val y = ph * h
            val band = 36.dp.toPx()
            val inset = 5.dp.toPx()
            clipRect(0f, 0f, w, h) {
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, ac.copy(alpha = 0.16f), Color.Transparent),
                        startY = y - band,
                        endY = y + band
                    ),
                    topLeft = Offset(0f, y - band),
                    size = Size(w, band * 2f)
                )
                drawLine(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, ac.copy(alpha = 0.75f), ac.copy(alpha = 0.75f), Color.Transparent),
                        startX = inset,
                        endX = w - inset
                    ),
                    Offset(inset, y),
                    Offset(w - inset, y),
                    1.3f.dp.toPx()
                )
            }
        }
        if (arrows) {
            val cy = h / 2f
            val hh = 9.dp.toPx()
            val hw = 5.dp.toPx()
            val sw = 2.2f.dp.toPx()
            val gap = 10.dp.toPx()
            val edge = 30.dp.toPx()
            for (i in 0 until 3) {
                val a = 1f - ((wv - i / 3f + 1f) % 1f)
                val col = ac.copy(alpha = 0.12f + 0.88f * a)
                val lx = -edge + i * gap
                drawLine(col, Offset(lx, cy - hh), Offset(lx + hw, cy), sw)
                drawLine(col, Offset(lx + hw, cy), Offset(lx, cy + hh), sw)
                val rx = w + edge - i * gap
                drawLine(col, Offset(rx, cy - hh), Offset(rx - hw, cy), sw)
                drawLine(col, Offset(rx - hw, cy), Offset(rx, cy + hh), sw)
            }
        }
    }
}
