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

package com.shoyoomc.aflecklauncher.ui.screens.content.settings.layouts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import com.shoyoomc.aflecklauncher.ui.screens.main.AfBorder
import com.shoyoomc.aflecklauncher.ui.screens.main.AfCutShape
import com.shoyoomc.aflecklauncher.ui.screens.main.afHud
import com.shoyoomc.aflecklauncher.ui.theme.onCardColor
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shoyoomc.aflecklauncher.ui.components.BackgroundCard
import com.shoyoomc.aflecklauncher.ui.components.TitleAndSummary

/**
 * 根据卡片在UI组中的位置，选择不同的圆角形状
 */
enum class CardPosition {
    /**
     * 位于 UI 组的顶部
     * ``` txt
     *   _______
     *  +       +
     * |         |
     * |         |
     * |         |
     *  ---------
     * ```
     */
    Top,

    /**
     * 位于 UI 组的顶部左侧
     * ``` txt
     *   ________
     *  +        |
     * |         |
     * |         |
     * |         |
     *  ---------
     * ```
     */
    TopStart,

    /**
     * 位于 UI 组的顶部右侧
     * ``` txt
     *  ________
     * |        +
     * |         |
     * |         |
     * |         |
     *  ---------
     * ```
     */
    TopEnd,

    /**
     * 位于 UI 组的中部
     * ``` txt
     *  _________
     * |         |
     * |         |
     * |         |
     * |         |
     *  ---------
     * ```
     */
    Middle,

    /**
     * 位于 UI 组的底部
     * ``` txt
     *  _________
     * |         |
     * |         |
     * |         |
     *  +       +
     *   -------
     * ```
     */
    Bottom,

    /**
     * 位于 UI 组的底部左侧
     * ``` txt
     *  _________
     * |         |
     * |         |
     * |         |
     *  +        |
     *   --------
     * ```
     */
    BottomStart,

    /**
     * 位于 UI 组的底部右侧
     * ``` txt
     *  _________
     * |         |
     * |         |
     * |         |
     * |        +
     *  --------
     * ```
     */
    BottomEnd,

    /**
     * 单个 UI 组件
     * ``` txt
     *   _______
     *  +       +
     * |         |
     * |         |
     *  +       +
     *   -------
     * ```
     */
    Single
}

/**
 * 根据 UI 组件在组中的位置决定的形状
 */
@Composable
fun rememberSettingsCardShape(
    position: CardPosition,
    outerShape: Dp = 28.dp,
    innerShape: Dp = 4.dp
): Shape {
    return remember(position) { AfCutShape(12.dp) }
}

/** Dentro de um [SettingsCardColumn] as linhas viram um único cartão de vidro com divisórias. */
internal val LocalSettingsGroup = compositionLocalOf { false }

@Composable
private fun SettingsGroupRow(
    modifier: Modifier,
    position: CardPosition,
    onClick: (() -> Unit)?,
    enabled: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    val first = position == CardPosition.Top || position == CardPosition.TopStart ||
            position == CardPosition.TopEnd || position == CardPosition.Single
    Column(
        modifier = modifier
            .heightIn(min = 55.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .then(if (onClick != null) Modifier.clickable(enabled = enabled, onClick = onClick) else Modifier)
    ) {
        if (!first) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(
                            listOf(
                                com.shoyoomc.aflecklauncher.ui.screens.main.AfAccent.copy(alpha = 0.6f),
                                AfBorder.copy(alpha = 0.25f),
                                androidx.compose.ui.graphics.Color.Transparent
                            )
                        )
                    )
            )
        }
        content()
    }
}

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    position: CardPosition,
    outerShape: Dp = 28.dp,
    innerShape: Dp = 4.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    if (LocalSettingsGroup.current) {
        SettingsGroupRow(modifier, position, null, true, content)
        return
    }
    val shape = rememberSettingsCardShape(position, outerShape, innerShape)

    BackgroundCard(
        modifier = modifier,
        shape = shape,
        content = content
    )
}

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    position: CardPosition,
    outerShape: Dp = 28.dp,
    innerShape: Dp = 4.dp,
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    if (LocalSettingsGroup.current) {
        SettingsGroupRow(modifier, position, onClick, enabled, content)
        return
    }
    val shape = rememberSettingsCardShape(position, outerShape, innerShape)

    BackgroundCard(
        modifier = modifier,
        shape = shape,
        onClick = onClick,
        enabled = enabled,
        content = content
    )
}

@Composable
fun SettingsCard(
    position: CardPosition,
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    titleStyle: TextStyle = MaterialTheme.typography.titleSmall,
    summaryStyle: TextStyle = MaterialTheme.typography.labelSmall,
    outerShape: Dp = 28.dp,
    innerShape: Dp = 4.dp,
    innerPadding: PaddingValues = PaddingValues(all = 16.dp),
    onClick: () -> Unit,
    trailingIcon: (@Composable RowScope.() -> Unit)? = null,
    enabled: Boolean = true
) {
    if (LocalSettingsGroup.current) {
        SettingsGroupRow(modifier, position, onClick, enabled) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TitleAndSummary(
                    modifier = Modifier.weight(1f),
                    title = title,
                    summary = summary,
                    titleStyle = titleStyle,
                    summaryStyle = summaryStyle
                )
                trailingIcon?.let { trailing ->
                    Row(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        content = trailing
                    )
                }
            }
        }
        return
    }
    val shape = rememberSettingsCardShape(position, outerShape, innerShape)

    BackgroundCard(
        modifier = modifier,
        shape = shape,
        onClick = onClick,
        enabled = enabled
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TitleAndSummary(
                modifier = Modifier.weight(1f),
                title = title,
                summary = summary,
                titleStyle = titleStyle,
                summaryStyle = summaryStyle
            )
            trailingIcon?.let { trailing ->
                Row(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    content = trailing
                )
            }
        }
    }
}

@Composable
fun SettingsCardColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    CompositionLocalProvider(
        LocalSettingsGroup provides true,
        LocalContentColor provides onCardColor()
    ) {
        Column(
            modifier = modifier
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.38f), AfCutShape(18.dp))
                .afHud(AfCutShape(18.dp))
                .drawBehind {
                    drawRect(
                        color = com.shoyoomc.aflecklauncher.ui.screens.main.AfAccent,
                        topLeft = androidx.compose.ui.geometry.Offset(0f, 22.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(2.5.dp.toPx(), (size.height - 44.dp.toPx()).coerceAtLeast(0f))
                    )
                    // colchetes de canto (visual HUD gamer)
                    val ac = com.shoyoomc.aflecklauncher.ui.screens.main.AfAccent
                    val t = 1.6.dp.toPx(); val l = 14.dp.toPx(); val m = 18.dp.toPx()
                    drawLine(ac, androidx.compose.ui.geometry.Offset(size.width - m - l, 0f), androidx.compose.ui.geometry.Offset(size.width - m, 0f), t)
                    drawLine(ac, androidx.compose.ui.geometry.Offset(size.width, m), androidx.compose.ui.geometry.Offset(size.width, m + l), t)
                    drawLine(ac, androidx.compose.ui.geometry.Offset(m, size.height), androidx.compose.ui.geometry.Offset(m + l, size.height), t)
                },
            verticalArrangement = Arrangement.spacedBy(0.dp),
            content = content
        )
    }
}
