package com.shoyoomc.aflecklauncher.ui.screens.main

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import android.content.SharedPreferences
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import com.shoyoomc.aflecklauncher.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Fonte compacta (condensada) para títulos, botões e rótulos. */
internal val AfCondensed: FontFamily = FontFamily(android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD))

/** Raio em px: pílula/arredondado grande (estilo redondo) ou 5 dp (estilo retangular). */
private fun afRadiusPx(w: Float, h: Float): Float {
    val d = android.content.res.Resources.getSystem().displayMetrics.density
    val r = if (AfThemeState.roundStyle) 26f * d else 5f * d
    return r.coerceAtMost(minOf(w, h) / 2f)
}

/** Contorno com cantos conforme o estilo escolhido (usado também para brilhos e molduras). */
internal fun afChamferPath(w: Float, h: Float, c0: Float): androidx.compose.ui.graphics.Path {
    val r = afRadiusPx(w, h)
    return androidx.compose.ui.graphics.Path().apply {
        addRoundRect(androidx.compose.ui.geometry.RoundRect(0f, 0f, w, h, androidx.compose.ui.geometry.CornerRadius(r, r)))
    }
}

/** Forma de botões, abas e ícones: pílula no estilo redondo, retângulo de 5 dp no outro. */
class AfHexShape(private val chamfer: Dp = 0.dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val r = afRadiusPx(size.width, size.height)
        return Outline.Rounded(androidx.compose.ui.geometry.RoundRect(0f, 0f, size.width, size.height, androidx.compose.ui.geometry.CornerRadius(r, r)))
    }
}

/** Definição de um tema aurora. */
class AfThemeDef(
    val id: String,
    val name: String,
    val bg: Int,
    val accent: Color,
    val accent2: Color,
    val base: Color,
    val border: Color,
    val dim: Color
)

/** Estado do tema (cores, vibração, contraste, opacidade), salvo no aparelho. */
object AfThemeState {
    val themes = listOf(
        // Tema principal Afleck Forja: preto profundo e grafite, neon laranja, cobre e rosa-salmão.
        AfThemeDef("afleck", "Afleck Forja", R.drawable.af_bg, Color(0xFFFF7A2A), Color(0xFFFF4A2B), Color(0xFF0B0605), Color(0xFFFF8A3D), Color(0xFFC9B2A3)),
        AfThemeDef("aurora", "Aurora Boreal", R.drawable.af_bg, Color(0xFF39FFB0), Color(0xFF8A5CFF), Color(0xFF041512), Color(0xFF39FFB0), Color(0xFFB6D8CE)),
        AfThemeDef("vermelha", "Aurora Rubi", R.drawable.af_bg, Color(0xFFFF4D5E), Color(0xFFFF8A5C), Color(0xFF2A060A), Color(0xFFFF4D5E), Color(0xFFE8C2C6)),
        AfThemeDef("laranja", "Aurora Âmbar", R.drawable.af_bg, Color(0xFFFF8A2B), Color(0xFFFFC02B), Color(0xFF2B1405), Color(0xFFFF8A2B), Color(0xFFE8D2BC)),
        AfThemeDef("amarela", "Aurora Solar", R.drawable.af_bg, Color(0xFFFFD740), Color(0xFFFF9F1C), Color(0xFF2A1E05), Color(0xFFFFC93C), Color(0xFFE6D8B0)),
        AfThemeDef("lima", "Aurora Lima", R.drawable.af_bg, Color(0xFFC6FF3D), Color(0xFF5EFF9A), Color(0xFF1A2605), Color(0xFFC6FF3D), Color(0xFFD6E4B4)),
        AfThemeDef("verde", "Aurora Esmeralda", R.drawable.af_bg, Color(0xFF3DFF8A), Color(0xFFB6FF5E), Color(0xFF07261A), Color(0xFF2BE67A), Color(0xFFB4DCC4)),
        AfThemeDef("turquesa", "Aurora Jade", R.drawable.af_bg, Color(0xFF2DF5C8), Color(0xFF2DB8F5), Color(0xFF05261F), Color(0xFF2DF5C8), Color(0xFFB4E0D6)),
        AfThemeDef("ciano", "Aurora Glacial", R.drawable.af_bg, Color(0xFF22E3F7), Color(0xFF4DF2D8), Color(0xFF0A3558), Color(0xFF1ED8F5), Color(0xFFB9D6E6)),
        AfThemeDef("azul", "Aurora Safira", R.drawable.af_bg, Color(0xFF4D8DFF), Color(0xFF22E3F7), Color(0xFF07163A), Color(0xFF4D8DFF), Color(0xFFBCCBE8)),
        AfThemeDef("anil", "Aurora Anil", R.drawable.af_bg, Color(0xFF6C5CFF), Color(0xFFB388FF), Color(0xFF0E0A3A), Color(0xFF6C5CFF), Color(0xFFC4C0E8)),
        AfThemeDef("roxa", "Aurora Ametista", R.drawable.af_bg, Color(0xFFB388FF), Color(0xFFFF7AE0), Color(0xFF1E0B3A), Color(0xFFB388FF), Color(0xFFD0C2E8)),
        AfThemeDef("magenta", "Aurora Orquídea", R.drawable.af_bg, Color(0xFFFF4DE0), Color(0xFFB388FF), Color(0xFF2E0B2A), Color(0xFFFF4DE0), Color(0xFFE8C0E2)),
        AfThemeDef("rosa", "Aurora Rosé", R.drawable.af_bg, Color(0xFFFF6EA8), Color(0xFFFFB36E), Color(0xFF3A0B22), Color(0xFFFF5C9A), Color(0xFFE8C2D2)),
        AfThemeDef("prata", "Aurora Prata", R.drawable.af_bg, Color(0xFFC9D1DB), Color(0xFF8E9AAB), Color(0xFF0C0F14), Color(0xFFB6C0CC), Color(0xFFADB6C2)),
        AfThemeDef("branca", "Aurora Pérola", R.drawable.af_bg, Color(0xFFFFFFFF), Color(0xFFBFC8D2), Color(0xFF0B0B0D), Color(0xFFE6ECF2), Color(0xFFB4B9C2))
    )

    var themeId by mutableStateOf("afleck")
        private set
    var vibrance by mutableIntStateOf(0)
        private set
    var contrast by mutableIntStateOf(0)
        private set
    var panelOpacity by mutableIntStateOf(72)
        private set
    var animate by mutableStateOf(false)
        private set
    var uiScale by mutableIntStateOf(100)
        private set
    var glowOn by mutableStateOf(false)
        private set
    var glowLevel by mutableIntStateOf(5)
        private set
    var glowStrength by mutableIntStateOf(70)
        private set
    var neonAnimated by mutableStateOf(false)
        private set
    var neonThickness by mutableIntStateOf(3)
        private set

    var auroraAnim by mutableStateOf(true)
        private set

    /** true = cantos bem arredondados (pílulas); false = retângulo de 5 dp. */
    var roundStyle by mutableStateOf(true)
        private set

    var dblClickOn by mutableStateOf(false)
        private set
    var dblClickRate by mutableIntStateOf(50)
        private set

    private var prefs: SharedPreferences? = null

    fun load(ctx: Context) {
        if (prefs != null) return
        val p = ctx.getSharedPreferences("afleck_ui", Context.MODE_PRIVATE)
        prefs = p
        themeId = p.getString("theme.id", "afleck") ?: "afleck"
        // Migra instalações anteriores para a identidade visual principal Afleck.
        if (p.getInt("theme.rev", 0) < 5) {
            themeId = "afleck"
            p.edit().putString("theme.id", "afleck").putInt("theme.rev", 5)
                .putInt("theme.panelOpacityGlass", 72).apply()
        }
        vibrance = p.getInt("theme.vibrance", 0)
        contrast = p.getInt("theme.contrast", 0)
        panelOpacity = p.getInt("theme.panelOpacityGlass", 72)
        animate = p.getBoolean("theme.animate", false)
        uiScale = p.getInt("theme.uiScale100", 100)
        glowOn = p.getBoolean("theme.glowOn", false)
        glowLevel = p.getInt("theme.glowLevel", 5)
        glowStrength = p.getInt("theme.glowStrength", 70)
        neonAnimated = p.getBoolean("theme.neonAnimated", false)
        neonThickness = p.getInt("theme.neonThickness", 3)
        auroraAnim = p.getBoolean("theme.auroraAnim", true)
        roundStyle = p.getBoolean("style.round", true)
        dblClickOn = p.getBoolean("game.dblClickOn", false)
        dblClickRate = p.getInt("game.dblClickGap", 50)
    }

    fun chooseTheme(id: String) { themeId = id; saveAll() }
    fun changeVibrance(v: Int) { vibrance = v.coerceIn(-100, 100); saveAll() }
    fun changeContrast(v: Int) { contrast = v.coerceIn(-100, 100); saveAll() }
    fun changePanelOpacity(v: Int) { panelOpacity = v.coerceIn(0, 100); saveAll() }
    fun changeAnimate(v: Boolean) { animate = v; saveAll() }
    fun changeGlowOn(v: Boolean) { glowOn = v; saveAll() }
    fun changeGlowLevel(v: Int) { glowLevel = v.coerceIn(1, 10); saveAll() }
    fun changeGlowStrength(v: Int) { glowStrength = v.coerceIn(5, 100); saveAll() }
    fun changeNeonAnimated(v: Boolean) { neonAnimated = v; saveAll() }
    fun changeNeonThickness(v: Int) { neonThickness = v.coerceIn(1, 10); saveAll() }
    fun changeAuroraAnim(v: Boolean) { auroraAnim = v; saveAll() }
    fun changeRoundStyle(v: Boolean) { roundStyle = v; saveAll() }
    fun changeDblClickOn(v: Boolean) { dblClickOn = v; saveAll() }
    fun changeDblClickRate(v: Int) { dblClickRate = v.coerceIn(20, 200); saveAll() }
    fun changeUiScale(v: Int) { uiScale = v.coerceIn(60, 110); saveAll() }
    fun resetAdjustments() { vibrance = 0; contrast = 0; panelOpacity = 72; animate = false; uiScale = 100; saveAll() }

    private fun saveAll() {
        prefs?.edit()
            ?.putString("theme.id", themeId)
            ?.putInt("theme.vibrance", vibrance)
            ?.putInt("theme.contrast", contrast)
            ?.putInt("theme.panelOpacityGlass", panelOpacity)
            ?.putBoolean("theme.animate", animate)
            ?.putInt("theme.uiScale100", uiScale)
            ?.putBoolean("theme.glowOn", glowOn)
            ?.putInt("theme.glowLevel", glowLevel)
            ?.putInt("theme.glowStrength", glowStrength)
            ?.putBoolean("theme.neonAnimated", neonAnimated)
            ?.putInt("theme.neonThickness", neonThickness)
            ?.putBoolean("theme.auroraAnim", auroraAnim)
            ?.putBoolean("style.round", roundStyle)
            ?.putBoolean("game.dblClickOn", dblClickOn)
            ?.putInt("game.dblClickGap", dblClickRate)
            ?.apply()
    }

    val current: AfThemeDef get() = themes.firstOrNull { it.id == themeId } ?: themes[0]

    private fun vivid(c: Color): Color {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(c.toArgb(), hsv)
        hsv[1] = (hsv[1] * (1f + vibrance / 100f)).coerceIn(0f, 1f)
        return Color(android.graphics.Color.HSVToColor(hsv))
    }

    private fun contrasted(c: Color): Color =
        if (contrast >= 0) lerp(c, Color.Black, 0.45f * contrast / 100f)
        else lerp(c, Color.White, 0.25f * -contrast / 100f)

    val accent: Color get() = vivid(current.accent)
    val accent2: Color get() = vivid(current.accent2)
    val border: Color get() = vivid(current.border)
    val base: Color get() = contrasted(current.base)
}

internal val AfPanel: Color get() = AfThemeState.base.copy(alpha = AfThemeState.panelOpacity / 100f)
internal val AfPanelDeep: Color get() = AfThemeState.base.copy(alpha = (AfThemeState.panelOpacity / 100f + 0.05f).coerceAtMost(1f))
internal val AfBorder: Color get() = AfThemeState.border
internal val AfAccent: Color get() = AfThemeState.accent
internal val AfAccent2: Color get() = AfThemeState.accent2
internal val AfNavy: Color get() = lerp(AfThemeState.base, Color.Black, 0.55f).copy(alpha = 1f)
internal val AfText = Color.White
internal val AfDim: Color get() = AfThemeState.current.dim

/** Degradê dos botões; se "Animar degradê" estiver ligado, as cores se movem. */
@Composable
internal fun afAccentBrush(): Brush {
    if (!AfThemeState.animate) return Brush.horizontalGradient(listOf(AfAccent, AfAccent2))
    val t = rememberInfiniteTransition(label = "afgrad")
    val off by t.animateFloat(0f, 800f, infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "afoff")
    return Brush.horizontalGradient(listOf(AfAccent, AfAccent2), startX = off, endX = off + 400f, tileMode = TileMode.Mirror)
}

internal fun Modifier.afClick(onClick: () -> Unit): Modifier = composed {
    clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick)
}

/** Painel de vidro azul-noite com borda cobre. */
@Composable
internal fun AfPanel(
    modifier: Modifier = Modifier,
    radius: Dp = 28.dp,
    deep: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(radius.coerceAtMost(28.dp))
    Box(
        modifier = modifier.afHudScreen(backing = if (deep) 0.82f else 0f, glow = false),
        content = content
    )
}


/** Vidro: fundo translúcido, brilho nas bordas e um reflexo fino no topo. */
internal fun Modifier.afGlassSurface(shape: androidx.compose.ui.graphics.Shape, selected: Boolean = false): Modifier = composed {
    val neon = AfThemeState.glowOn && AfThemeState.neonAnimated
    val pulse = if (neon) rememberInfiniteTransition(label = "afneon").animateFloat(
        initialValue = 0.45f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), androidx.compose.animation.core.RepeatMode.Reverse),
        label = "afneonv"
    ) else null
    val k = (AfThemeState.panelOpacity / 45f).coerceIn(0.3f, 1.8f)
    val accent = AfAccent
    val accent2 = AfAccent2
    val base = AfThemeState.base
    val border = AfBorder
    val fill = remember(selected, k, accent, accent2) {
        if (selected)
            Brush.horizontalGradient(listOf(accent.copy(alpha = 0.95f), accent2.copy(alpha = 0.95f)))
        else
            Brush.verticalGradient(
                listOf(
                    Color.Black.copy(alpha = (0.42f * k).coerceAtMost(0.80f)),
                    Color.Black.copy(alpha = (0.34f * k).coerceAtMost(0.70f))
                )
            )
    }
    val borderBrush = remember(selected, border, accent, accent2) {
        if (selected) Brush.linearGradient(listOf(Color.White.copy(alpha = 0.7f), accent.copy(alpha = 1f)))
        else Brush.linearGradient(listOf(accent.copy(alpha = 0.85f), accent2.copy(alpha = 0.50f), accent.copy(alpha = 0.85f)))
    }
    val glintBrush = remember { Brush.horizontalGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.95f), Color.Transparent)) }
    val glowOn = AfThemeState.glowOn
    val glowLevel = AfThemeState.glowLevel.coerceAtMost(4)
    val glowStr = AfThemeState.glowStrength / 100f
    val glowTh = AfThemeState.neonThickness / 3f
    this@composed
        .then(if (selected) Modifier.shadow(12.dp, shape, ambientColor = accent, spotColor = accent) else Modifier)
        .clip(shape)
        .background(fill, shape)
        .drawWithCache {
            val o = shape.createOutline(size, layoutDirection, this)
            val borderStroke = androidx.compose.ui.graphics.drawscope.Stroke(1.2.dp.toPx())
            val y = 1.dp.toPx()
            val p1 = androidx.compose.ui.geometry.Offset(size.width * 0.18f, y)
            val p2 = androidx.compose.ui.geometry.Offset(size.width * 0.82f, y)
            val glintW = 1.6.dp.toPx()
            onDrawWithContent {
                drawContent()
                afOutline(o, borderBrush, style = borderStroke)
                if (glowOn) {
                    val pv = pulse?.value ?: 1f
                    for (i in 1..glowLevel) {
                        val al = (0.34f * glowStr * pv / i).coerceIn(0f, 1f)
                        afOutline(
                            o,
                            Brush.verticalGradient(listOf(accent.copy(alpha = al), accent2.copy(alpha = al))),
                            style = androidx.compose.ui.graphics.drawscope.Stroke((i * 1.5f * glowTh).dp.toPx())
                        )
                    }
                }
            }
        }
}

/** Retângulo arredondado só com borda (sem fundo, sem sombra). */
internal fun Modifier.afRect(color: Color, selected: Boolean = false, radius: Dp = 14.dp): Modifier =
    this.afGlassSurface(AfHexShape(), selected)

/** Vidro bem escuro com a cor do tema (marca, menu lateral): fundo translúcido, mas denso, como nas referências Afleck. */
internal fun Modifier.afDarkGlass(color: Color, selected: Boolean = false, radius: Dp = 28.dp): Modifier =
    this.afGlassSurface(AfHexShape(), selected)

/** Botão principal: degradê âmbar/coral, texto azul-noite como nas referências. */
@Composable
internal fun AfButton(text: String, modifier: Modifier = Modifier, icon: Int? = null, onClick: () -> Unit) {
    Row(
        modifier = modifier.height(55.dp)
            .afRect(AfAccent, selected = true, radius = 14.dp)
            .afClick(onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(painterResource(icon), null, tint = AfNavy, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text.uppercase(), color = AfNavy, fontSize = 14.sp, fontFamily = AfCondensed, letterSpacing = 1.sp, maxLines = 1)
    }
}

/** Botão secundário: contorno cobre discreto. */
@Composable
internal fun AfOutlineButton(text: String, modifier: Modifier = Modifier, icon: Int? = null, onClick: () -> Unit) {
    Row(
        modifier = modifier.height(55.dp)
            .afRect(AfBorder, radius = 14.dp)
            .afClick(onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(painterResource(icon), null, tint = AfAccent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text.uppercase(), color = AfText, fontSize = 13.sp, fontFamily = AfCondensed, letterSpacing = 1.sp, maxLines = 1)
    }
}

/** Círculo com ícone (preenchido em âmbar ou escuro com contorno cobre). */
@Composable
internal fun AfIconCircle(icon: Int, size: Dp = 40.dp, filled: Boolean = false, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(size)
            .afGlow(AfHexShape(), if (filled) AfAccent else AfBorder, if (filled) 1.2f else 0.7f)
            .clip(AfHexShape())
            .background(if (filled) AfAccent.copy(alpha = 0.16f) else Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(painterResource(icon), null, tint = if (filled) AfAccent else AfText, modifier = Modifier.size(size * 0.5f))
    }
}

/** Painel "vidro HUD": degradê de vidro, brilho no topo e cantoneiras neon nos cantos. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.afOutline(
    outline: androidx.compose.ui.graphics.Outline,
    color: Color,
    style: androidx.compose.ui.graphics.drawscope.DrawStyle = androidx.compose.ui.graphics.drawscope.Fill
) = drawPath(outline.afPath(), color, style = style)

private fun androidx.compose.ui.graphics.drawscope.DrawScope.afOutline(
    outline: androidx.compose.ui.graphics.Outline,
    brush: Brush,
    style: androidx.compose.ui.graphics.drawscope.DrawStyle = androidx.compose.ui.graphics.drawscope.Fill
) = drawPath(outline.afPath(), brush, style = style)

private fun androidx.compose.ui.graphics.Outline.afPath(): androidx.compose.ui.graphics.Path = when (this) {
    is androidx.compose.ui.graphics.Outline.Rectangle -> androidx.compose.ui.graphics.Path().apply { addRect(rect) }
    is androidx.compose.ui.graphics.Outline.Rounded -> androidx.compose.ui.graphics.Path().apply { addRoundRect(roundRect) }
    is androidx.compose.ui.graphics.Outline.Generic -> path
}

/**
 * Vidro fino (pílulas, botões, linhas): fundo claro translúcido, borda fina clara,
 * reflexo no canto de cima e brilho no meio da base. [strength] >= 1.2 = selecionado (tingido com [color]).
 */
internal fun Modifier.afGlow(shape: androidx.compose.ui.graphics.Shape, color: Color, strength: Float = 1f): Modifier =
    this.drawWithContent {
        drawContent()
        val o = shape.createOutline(size, layoutDirection, this)
        val sel = strength >= 1.2f
        val tint = lerp(Color.White, color, if (sel) 0.85f else 0.35f)
        val w = size.width
        val h = size.height
        val thin = 1.dp.toPx()
        clipPath(o.afPath()) {
            if (sel) drawRect(color.copy(alpha = 0.16f))
        }
        // borda fina: clara em cima, mais suave embaixo
        afOutline(
            o,
            Brush.verticalGradient(listOf(tint.copy(alpha = 0.85f), tint.copy(alpha = 0.28f), tint.copy(alpha = 0.5f))),
            style = androidx.compose.ui.graphics.drawscope.Stroke(thin)
        )
    }

/** Moldura arredondada (estilo HUD Afleck). Mantém o nome AfCutShape para valer em todas as telas. */
class AfCutShape(private val cut: Dp = 14.dp) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(afChamferPath(size.width, size.height, with(density) { (cut * 0.8f).coerceAtMost(12.dp).toPx() }))
}

/** Vidro grande (cartões e painéis): degradê escuro na diagonal, luz nas bordas, borda branca fina. */
@Composable
internal fun Modifier.afHud(shape: androidx.compose.ui.graphics.Shape): Modifier = this.afGlassSurface(shape).afScanLine()

/** Relógio único da linha de varredura (um só para todos os cartões; ~30 quadros por segundo). */
internal object AfScan { val t = androidx.compose.runtime.mutableFloatStateOf(0f) }

@Composable
internal fun AfScanClock() {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        var last = 0L
        while (true) {
            androidx.compose.runtime.withFrameNanos { n ->
                if (n - last >= 33_000_000L) { last = n; AfScan.t.floatValue = n / 1_000_000_000f }
            }
        }
    }
}

/** Linha na cor do tema que sobe e desce no fundo do cartão (cada cartão com uma fase diferente). */
internal fun Modifier.afScanLine(): Modifier = this.drawBehind {
    val h = size.height
    val w = size.width
    if (h <= 0f || w <= 0f) return@drawBehind
    val period = 5.5f
    val ph = ((AfScan.t.floatValue / period) + (h * 0.0013f) + (w * 0.0007f)) % 1f
    val tri = if (ph < 0.5f) ph * 2f else 2f - ph * 2f
    val y = tri * h
    val ac = AfAccent
    val band = 38.dp.toPx()
    val inset = 5.dp.toPx()
    androidx.compose.ui.graphics.drawscope.clipRect(0f, 0f, w, h) {
        drawRect(
            Brush.verticalGradient(
                listOf(Color.Transparent, ac.copy(alpha = 0.16f), Color.Transparent),
                startY = y - band, endY = y + band
            ),
            topLeft = Offset(0f, y - band), size = Size(w, band * 2f)
        )
        drawLine(
            Brush.horizontalGradient(
                listOf(Color.Transparent, ac.copy(alpha = 0.75f), ac.copy(alpha = 0.75f), Color.Transparent),
                startX = inset, endX = w - inset
            ),
            Offset(inset, y), Offset(w - inset, y), 1.3.dp.toPx()
        )
    }
}


/** Cor firme (mas na cor do tema) para janelas por cima de outras telas. */
internal val AfDialogColor: Color get() = androidx.compose.ui.graphics.lerp(AfThemeState.base, AfAccent, 0.14f).copy(alpha = 0.84f)

internal fun Modifier.afDialogBacking(shape: androidx.compose.ui.graphics.Shape): Modifier =
    this.clip(shape).background(AfDialogColor, shape)

/** Moldura Afleck Forja: painel escuro com cantos chanfrados e contorno neon laranja com luz difusa. */
class AfHudFrameShape : androidx.compose.ui.graphics.Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(afChamferPath(size.width, size.height, with(density) { 12.dp.toPx() }))
}

private fun Modifier.afForgeFrame(backing: Float, glow: Boolean): Modifier = composed {
    val accent = AfAccent
    val accent2 = AfAccent2
    val shape = remember { AfHudFrameShape() }
    val tint = Color.Black.copy(alpha = (0.30f + backing * 0.55f).coerceIn(0f, 0.92f))
    this@composed
        // luz externa difusa neon
        .drawBehind {
            if (glow) {
                val path = afChamferPath(size.width, size.height, 12.dp.toPx())
                drawPath(path, accent.copy(alpha = 0.14f), style = androidx.compose.ui.graphics.drawscope.Stroke(18.dp.toPx()))
                drawPath(path, accent.copy(alpha = 0.26f), style = androidx.compose.ui.graphics.drawscope.Stroke(9.dp.toPx()))
            }
        }
        .clip(shape)
        .background(tint, shape)
        // luz interna
        .background(Brush.verticalGradient(listOf(accent.copy(alpha = 0.09f), Color.Transparent, accent2.copy(alpha = 0.05f))), shape)
        .drawWithCache {
            val u = 1.dp.toPx()
            val path = afChamferPath(size.width, size.height, 12 * u)
            val contour = Brush.linearGradient(listOf(accent.copy(alpha = 0.95f), accent2.copy(alpha = 0.75f), accent.copy(alpha = 0.95f)))
            val thin = androidx.compose.ui.graphics.drawscope.Stroke(1.6f * u)
            val soft = androidx.compose.ui.graphics.drawscope.Stroke(4f * u)
            onDrawWithContent {
                drawContent()
                drawPath(path, accent.copy(alpha = 0.18f), style = soft)
                drawPath(path, contour, style = thin)
                drawPath(path, Color(0xFFFFE2D2).copy(alpha = 0.30f), style = androidx.compose.ui.graphics.drawscope.Stroke(0.7f * u))
            }
        }
}

internal fun Modifier.afHudFrame(backing: Float = 0.55f): Modifier = this.afForgeFrame(backing, true)

internal fun Modifier.afHudScreen(backing: Float = 0.6f, glow: Boolean = true): Modifier = this.afForgeFrame(backing, glow)

/** Botão de perigo (excluir, apagar): rosa avermelhado, alto contraste. */
@Composable
internal fun AfDangerButton(text: String, modifier: Modifier = Modifier, icon: Int? = null, onClick: () -> Unit) {
    val red = Color(0xFFFF4D6A)
    val shape = AfHexShape()
    Row(
        modifier = modifier.height(44.dp)
            .shadow(10.dp, shape, ambientColor = red, spotColor = red)
            .clip(shape)
            .background(Brush.horizontalGradient(listOf(Color(0xFFFF4D6A), Color(0xFFD7263D))), shape)
            .afClick(onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(painterResource(icon), null, tint = Color.White, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text.uppercase(), color = Color.White, fontSize = 13.sp, fontFamily = AfCondensed, letterSpacing = 1.sp, maxLines = 1)
    }
}
