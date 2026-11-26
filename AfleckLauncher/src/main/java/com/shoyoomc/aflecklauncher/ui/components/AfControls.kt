/*
 * AFLECK LAUNCHER — controles HUD próprios (seletor, caixa de marcação, seta).
 * Baseado no Zalith Launcher 2 (GPLv3), mas todos os controles visuais são originais.
 */

package com.shoyoomc.aflecklauncher.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shoyoomc.aflecklauncher.ui.screens.main.AfAccent
import com.shoyoomc.aflecklauncher.ui.screens.main.AfBorder
import com.shoyoomc.aflecklauncher.ui.screens.main.AfDim
import com.shoyoomc.aflecklauncher.ui.screens.main.AfNavy
import kotlin.math.cos
import kotlin.math.sin

private fun hexPath(cx: Float, cy: Float, r: Float): Path = Path().apply {
    for (k in 0 until 6) {
        val a = Math.toRadians(-90.0 + 60.0 * k)
        val x = cx + r * cos(a).toFloat()
        val y = cy + r * sin(a).toFloat()
        if (k == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

/** Seletor hexagonal: vazio = só contorno; marcado = hexágono laranja com núcleo aceso. */
@Composable
fun AfSelector(selected: Boolean, modifier: Modifier = Modifier, sizeDp: Dp = 22.dp) {
    val accent = AfAccent
    val dim = AfDim
    Canvas(modifier.size(sizeDp)) {
        val c = Offset(this.size.width / 2f, this.size.height / 2f)
        val r = this.size.minDimension / 2f - 1.5.dp.toPx()
        if (selected) {
            drawPath(hexPath(c.x, c.y, r), accent.copy(alpha = 0.22f))
            drawPath(hexPath(c.x, c.y, r), accent, style = Stroke(1.8.dp.toPx(), join = StrokeJoin.Round))
            drawPath(hexPath(c.x, c.y, r * 0.48f), accent)
        } else {
            drawPath(hexPath(c.x, c.y, r), dim.copy(alpha = 0.85f), style = Stroke(1.5.dp.toPx(), join = StrokeJoin.Round))
        }
    }
}

/** Caixa de marcação hexagonal com "check" desenhado. */
@Composable
fun AfCheckMark(checked: Boolean, modifier: Modifier = Modifier, sizeDp: Dp = 22.dp) {
    val accent = AfAccent
    val dim = AfDim
    val navy = AfNavy
    Canvas(modifier.size(sizeDp)) {
        val c = Offset(this.size.width / 2f, this.size.height / 2f)
        val r = this.size.minDimension / 2f - 1.5.dp.toPx()
        if (checked) {
            drawPath(hexPath(c.x, c.y, r), accent)
            val chk = Path().apply {
                moveTo(c.x - r * 0.38f, c.y + r * 0.02f)
                lineTo(c.x - r * 0.08f, c.y + r * 0.32f)
                lineTo(c.x + r * 0.42f, c.y - r * 0.28f)
            }
            drawPath(chk, navy, style = Stroke(2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        } else {
            drawPath(hexPath(c.x, c.y, r), dim.copy(alpha = 0.85f), style = Stroke(1.5.dp.toPx(), join = StrokeJoin.Round))
        }
    }
}

/** Seta de lista: hexágono com chevron. Gire com Modifier.rotate para abrir/fechar. */
@Composable
fun AfChevron(modifier: Modifier = Modifier, sizeDp: Dp = 24.dp) {
    val accent = AfAccent
    val border = AfBorder
    Canvas(modifier.size(sizeDp)) {
        val c = Offset(this.size.width / 2f, this.size.height / 2f)
        val r = this.size.minDimension / 2f - 1.dp.toPx()
        drawPath(hexPath(c.x, c.y, r), border.copy(alpha = 0.9f), style = Stroke(1.3.dp.toPx(), join = StrokeJoin.Round))
        val ch = Path().apply {
            moveTo(c.x - r * 0.38f, c.y - r * 0.14f)
            lineTo(c.x, c.y + r * 0.24f)
            lineTo(c.x + r * 0.38f, c.y - r * 0.14f)
        }
        drawPath(ch, accent, style = Stroke(1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** Marcador pequeno (losango) usado antes dos títulos das opções. */
@Composable
fun AfBullet(modifier: Modifier = Modifier, sizeDp: Dp = 7.dp) {
    val accent = AfAccent
    Canvas(modifier.size(sizeDp)) {
        val w = this.size.width
        val p = Path().apply {
            moveTo(w / 2f, 0f); lineTo(w, w / 2f); lineTo(w / 2f, w); lineTo(0f, w / 2f); close()
        }
        drawPath(p, accent)
    }
}

/** Substitui o RadioButton do Material: mesmo contrato, visual hexagonal próprio. */
@Composable
fun AfRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColors = RadioButtonDefaults.colors(),
    interactionSource: MutableInteractionSource? = null
) {
    val m = if (onClick != null)
        modifier.selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
    else modifier
    Box(m.size(44.dp).alpha(if (enabled) 1f else 0.4f), contentAlignment = Alignment.Center) {
        AfSelector(selected)
    }
}

/** Substitui o Checkbox do Material: mesmo contrato, visual hexagonal próprio. */
@Composable
fun AfCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColors = CheckboxDefaults.colors(),
    interactionSource: MutableInteractionSource? = null
) {
    val m = if (onCheckedChange != null)
        modifier.toggleable(value = checked, enabled = enabled, role = Role.Checkbox, onValueChange = onCheckedChange)
    else modifier
    Box(m.size(44.dp).alpha(if (enabled) 1f else 0.4f), contentAlignment = Alignment.Center) {
        AfCheckMark(checked)
    }
}
