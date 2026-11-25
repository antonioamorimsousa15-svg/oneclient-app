/*
 * Zalith Launcher 2
 * Copyright (C) 2025-2026 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.shoyoomc.aflecklauncher.ui.components

import androidx.annotation.IntRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.shoyoomc.aflecklauncher.ui.screens.main.AfAccent
import com.shoyoomc.aflecklauncher.ui.screens.main.AfAccent2
import com.shoyoomc.aflecklauncher.ui.screens.main.AfBorder
import com.shoyoomc.aflecklauncher.ui.screens.main.afClick
import com.shoyoomc.aflecklauncher.ui.screens.main.afGlow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.shoyoomc.aflecklauncher.R
import com.shoyoomc.aflecklauncher.ui.screens.content.elements.DisabledAlpha
import com.shoyoomc.aflecklauncher.utils.math.addBigDecimal
import com.shoyoomc.aflecklauncher.utils.math.subtractBigDecimal
import java.text.DecimalFormat

/**
 * 简单的文本滑动条，支持实时显示当前滑动条的数值，支持显示自定义数值的单位
 * @param shorter 是否使用更短的指示器的滑动条
 */
@Composable
fun SimpleTextSlider(
    modifier: Modifier = Modifier,
    shorter: Boolean = false,
    value: Float,
    decimalFormat: String = "#0.00",
    enabled: Boolean = true,
    onValueChange: (Float) -> Unit,
    toInt: Boolean = false,
    suffix: String? = null,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    onTextClick: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    fineTuningControl: Boolean = false,
    fineTuningStep: Float = 0.5f,
    valueText: ((Float) -> String)? = null,
    appendContent: @Composable () -> Unit = {}
) {
    val formatter = remember(decimalFormat) { DecimalFormat(decimalFormat) }
    fun getTextString(value: Float) = valueText?.invoke(value) ?: "${if (toInt) {
        value.toInt()
    } else {
        formatter.format(value)
    }}"

    fun changeValue(newValue: Float, finished: Boolean) {
        onValueChange(newValue)
        if (finished) onValueChangeFinished?.invoke()
    }

    LaunchedEffect(Unit) {
        //检查值是否被刻意的修改为超出范围
        if (value !in valueRange) {
            val newValue = value.coerceIn(valueRange)
            //调回范围内
            changeValue(newValue, true)
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val labelFor = { v: Float -> getTextString(v) + (suffix ?: "") }
        AfSlider(
            modifier = Modifier.weight(1f),
            value = value,
            enabled = enabled,
            onValueChange = { changeValue(it, false) },
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            steps = steps,
            labelText = labelFor
        )

        Row(
            modifier = Modifier
                .alpha(alpha = if (enabled) 1f else DisabledAlpha)
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (fineTuningControl) {
                AfStepButton(enabled = enabled, icon = R.drawable.ic_arrow_left_rounded) {
                    val newValue = value.subtractBigDecimal(fineTuningStep)
                    changeValue(if (newValue <= valueRange.start) valueRange.start else newValue, true)
                }
            }
            Row(
                modifier = Modifier
                    .widthIn(min = 64.dp)
                    .then(
                        if (onTextClick != null) {
                            Modifier.clickable(enabled = enabled, onClick = onTextClick)
                        } else Modifier
                    ),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = getTextString(value),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                suffix?.let { text ->
                    Text(
                        modifier = Modifier.padding(start = 2.dp, bottom = 3.dp),
                        text = text,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            if (fineTuningControl) {
                AfStepButton(enabled = enabled, icon = R.drawable.ic_arrow_right_rounded) {
                    val newValue = value.addBigDecimal(fineTuningStep)
                    changeValue(if (newValue >= valueRange.endInclusive) valueRange.endInclusive else newValue, true)
                }
            }
        }
        appendContent()
    }
}

@Composable
private fun AfStepButton(enabled: Boolean, icon: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .afGlow(CircleShape, AfBorder, 0.7f)
            .clip(CircleShape)
            .then(if (enabled) Modifier.afClick(onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Barra HUD: marcas de régua em cima, linha fina com extremidades, preenchimento em degradê
 * com brilho e seletor hexagonal, mínimo/meio/máximo embaixo.
 */
@Composable
fun AfSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean = true,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    labelText: (Float) -> String
) {
    val accent = AfAccent
    val accent2 = AfAccent2
    val border = AfBorder
    val textColor = LocalContentColor.current
    val span = valueRange.endInclusive - valueRange.start
    val frac = if (span <= 0f) 0f else ((value - valueRange.start) / span).coerceIn(0f, 1f)
    var widthPx by remember { mutableFloatStateOf(1f) }
    val padPx = with(LocalDensity.current) { 14.dp.toPx() }
    val onChange by rememberUpdatedState(onValueChange)
    val onFinish by rememberUpdatedState(onValueChangeFinished)

    fun toValue(x: Float): Float {
        val f = ((x - padPx) / (widthPx - 2 * padPx).coerceAtLeast(1f)).coerceIn(0f, 1f)
        val snapped = if (steps > 0) {
            val n = steps + 1
            Math.round(f * n) / n.toFloat()
        } else f
        return valueRange.start + snapped * span
    }

    Column(modifier = modifier.alpha(if (enabled) 1f else DisabledAlpha)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .onSizeChanged { widthPx = it.width.toFloat() }
                .pointerInput(enabled, steps, valueRange) {
                    if (!enabled) return@pointerInput
                    detectTapGestures { pos ->
                        onChange(toValue(pos.x))
                        onFinish?.invoke()
                    }
                }
                .pointerInput(enabled, steps, valueRange) {
                    if (!enabled) return@pointerInput
                    detectHorizontalDragGestures(
                        onDragEnd = { onFinish?.invoke() },
                        onDragCancel = { onFinish?.invoke() }
                    ) { change, _ ->
                        change.consume()
                        onChange(toValue(change.position.x))
                    }
                }
                .drawBehind {
                    val w = size.width
                    val left = padPx
                    val right = w - padPx
                    val trackY = size.height * 0.66f
                    val thumbX = left + frac * (right - left)
                    val dp1 = 1.dp.toPx()

                    //régua
                    for (i in 0..10) {
                        val x = left + (right - left) * i / 10f
                        val big = i == 0 || i == 5 || i == 10
                        drawLine(
                            color = border.copy(alpha = if (big) 0.95f else 0.55f),
                            start = Offset(x, size.height * (if (big) 0.10f else 0.20f)),
                            end = Offset(x, size.height * 0.32f),
                            strokeWidth = dp1 * (if (big) 1.6f else 1f)
                        )
                    }
                    //linha base HUD
                    drawLine(
                        color = border.copy(alpha = 0.45f),
                        start = Offset(left, trackY),
                        end = Offset(right, trackY),
                        strokeWidth = dp1 * 1.5f
                    )
                    //segmentos técnicos
                    var x = left + 18.dp.toPx()
                    while (x < right - 18.dp.toPx()) {
                        drawLine(
                            color = border.copy(alpha = 0.35f),
                            start = Offset(x, trackY - 4.dp.toPx()),
                            end = Offset(x + 6.dp.toPx(), trackY - 4.dp.toPx()),
                            strokeWidth = dp1
                        )
                        x += 46.dp.toPx()
                    }
                    //extremidades: setas
                    val arrow = Path().apply {
                        moveTo(left + 6.dp.toPx(), trackY - 6.dp.toPx())
                        lineTo(left - 4.dp.toPx(), trackY)
                        lineTo(left + 6.dp.toPx(), trackY + 6.dp.toPx())
                    }
                    drawPath(arrow, color = border.copy(alpha = 0.9f), style = Stroke(width = dp1 * 1.6f, cap = StrokeCap.Round))
                    val arrowR = Path().apply {
                        moveTo(right - 6.dp.toPx(), trackY - 6.dp.toPx())
                        lineTo(right + 4.dp.toPx(), trackY)
                        lineTo(right - 6.dp.toPx(), trackY + 6.dp.toPx())
                    }
                    drawPath(arrowR, color = border.copy(alpha = 0.9f), style = Stroke(width = dp1 * 1.6f, cap = StrokeCap.Round))

                    //preenchimento com brilho
                    if (thumbX > left) {
                        drawLine(
                            color = accent.copy(alpha = 0.30f),
                            start = Offset(left, trackY),
                            end = Offset(thumbX, trackY),
                            strokeWidth = dp1 * 9f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(accent, accent2),
                                startX = left,
                                endX = right
                            ),
                            start = Offset(left, trackY),
                            end = Offset(thumbX, trackY),
                            strokeWidth = dp1 * 3.5f,
                            cap = StrokeCap.Round
                        )
                    }

                    //seletor circular
                    val r = 11.dp.toPx()
                    drawCircle(accent.copy(alpha = 0.28f), radius = r * 1.9f, center = Offset(thumbX, trackY))
                    drawCircle(Color.White, radius = r, center = Offset(thumbX, trackY))
                    drawCircle(accent2, radius = r, center = Offset(thumbX, trackY), style = Stroke(width = dp1 * 3f))
                    drawCircle(Color(0xFF0A0F14), radius = r * 0.42f, center = Offset(thumbX, trackY))
                }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val small = MaterialTheme.typography.labelSmall
            Text(labelText(valueRange.start), style = small, color = textColor.copy(alpha = 0.75f))
            Text(
                labelText((valueRange.start + valueRange.endInclusive) / 2f),
                style = small, color = textColor.copy(alpha = 0.75f), textAlign = TextAlign.Center
            )
            Text(labelText(valueRange.endInclusive), style = small, color = textColor.copy(alpha = 0.75f))
        }
    }
}

@Composable
fun IndicatorSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    @IntRange(from = 0) steps: Int = 0,
    colors: SliderColors = SliderDefaults.colors()
) {
    /** Slider顶部需要裁切的像素 */
    val sliderTopCut = with(LocalDensity.current) { 8.dp.toPx().toInt() }
    /** Slider底部需要裁切的像素 */
    val sliderBottomCut = with(LocalDensity.current) { 6.dp.toPx().toInt() }
    Layout(
        modifier = modifier,
        content = {
            val state = rememberSliderState(
                value = value,
                steps = steps,
                trackRange = valueRange,
            )
            Slider(
                state = state,
                onValueChange = { value ->
                    onValueChange(value)
                    state.value = value
                },
                enabled = enabled,
                onValueChangeFinished = onValueChangeFinished,
                interactionSource = interactionSource,
                colors = colors,
                thumb = {
                    SliderDefaults.Thumb(
                        interactionSource = interactionSource,
                        colors = colors,
                        enabled = enabled,
                        thumbSize = DpSize(4.0.dp, 18.5.dp)
                    )
                }
            )
        }
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints)
        val newHeight = (placeable.height - sliderTopCut - sliderBottomCut).coerceAtLeast(0)
        layout(placeable.width, newHeight) {
            placeable.place(0, -sliderTopCut)
        }
    }
}