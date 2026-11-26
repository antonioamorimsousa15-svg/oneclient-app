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

package com.shoyoomc.aflecklauncher.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.sp

/** Títulos e rótulos em fonte compacta (condensada); o texto corrido continua legível. */
val AppTypography = Typography().let { t ->
    val c = com.shoyoomc.aflecklauncher.ui.screens.main.AfCondensed
    t.copy(
        headlineLarge = t.headlineLarge.copy(fontFamily = c), headlineMedium = t.headlineMedium.copy(fontFamily = c), headlineSmall = t.headlineSmall.copy(fontFamily = c),
        titleLarge = t.titleLarge.copy(fontFamily = c), titleMedium = t.titleMedium.copy(fontFamily = c), titleSmall = t.titleSmall.copy(fontFamily = c),
        labelLarge = t.labelLarge.copy(fontFamily = c, letterSpacing = 0.8.sp)
    )
}
