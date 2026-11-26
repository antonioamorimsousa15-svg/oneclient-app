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

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.shoyoomc.aflecklauncher.R

@Composable
fun DefaultSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SwitchColors = SwitchDefaults.colors(),
    interactionSource: MutableInteractionSource? = null
) {
    // Interruptor técnico: trilho retangular com borda fina e botão quadrado
    val on = MaterialTheme.colorScheme.primary
    val off = MaterialTheme.colorScheme.outline
    val c = if (!enabled) off.copy(alpha = 0.3f) else if (checked) on else off
    val shape = com.shoyoomc.aflecklauncher.ui.screens.main.AfHexShape()
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .size(width = 46.dp, height = 24.dp)
            .clip(shape)
            .background(if (checked) on.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.5f))
            .border(1.dp, c, shape)
            .toggleable(
                value = checked, enabled = enabled, role = Role.Switch,
                interactionSource = interactionSource, indication = null, onValueChange = onCheckedChange
            )
            .padding(3.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            Modifier.size(width = 20.dp, height = 16.dp)
                .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                .background(c, com.shoyoomc.aflecklauncher.ui.screens.main.AfHexShape())
        )
    }
}

/**
 * Represents the color used for the switch's track, depending on [enabled] and [checked].
 *
 * @param enabled whether the [Switch] is enabled or not
 * @param checked whether the [Switch] is checked or not
 */
@Stable
private fun SwitchColors.trackColor(enabled: Boolean, checked: Boolean): Color =
    if (enabled) {
        if (checked) checkedTrackColor else uncheckedTrackColor
    } else {
        if (checked) disabledCheckedTrackColor else disabledUncheckedTrackColor
    }