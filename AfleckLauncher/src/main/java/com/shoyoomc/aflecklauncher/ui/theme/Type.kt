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

private val AfMono = androidx.compose.ui.text.font.FontFamily.Monospace

private fun androidx.compose.ui.text.TextStyle.af() = copy(fontFamily = AfMono, letterSpacing = 0.4.sp)

/** Tipografia técnica do Afleck: monoespaçada em todo o app. */
val AppTypography = Typography().let { t ->
    t.copy(
        displayLarge = t.displayLarge.af(), displayMedium = t.displayMedium.af(), displaySmall = t.displaySmall.af(),
        headlineLarge = t.headlineLarge.af(), headlineMedium = t.headlineMedium.af(), headlineSmall = t.headlineSmall.af(),
        titleLarge = t.titleLarge.af(), titleMedium = t.titleMedium.af(), titleSmall = t.titleSmall.af(),
        bodyLarge = t.bodyLarge.af(), bodyMedium = t.bodyMedium.af(), bodySmall = t.bodySmall.af(),
        labelLarge = t.labelLarge.af(), labelMedium = t.labelMedium.af(), labelSmall = t.labelSmall.af()
    )
}
