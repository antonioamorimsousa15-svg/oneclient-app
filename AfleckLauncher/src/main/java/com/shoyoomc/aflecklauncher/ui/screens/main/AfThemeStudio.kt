package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlin.math.roundToInt

/** "Estúdio de temas": janela com a lista de temas, prévia da tela inicial e ajustes de cor/degradê. */
@Composable
fun AfThemeStudioDialog(onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(
            Modifier.fillMaxWidth(0.82f).fillMaxHeight(0.88f)
                .afHudScreen(backing = 0f, glow = true)
                .padding(5.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            AfAuroraBackground(Modifier.matchParentSize())
            Row(Modifier.fillMaxSize().padding(14.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                // ----- esquerda: lista de temas
                Column(Modifier.weight(0.9f).fillMaxHeight()) {
                    Text("Estúdio de temas", color = AfText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Escolha o tema do Launcher", color = AfDim, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Column(
                        Modifier.weight(1f).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AfThemeState.themes.forEach { t ->
                            val selected = t.id == AfThemeState.themeId
                            val shape = AfCutShape(12.dp)
                            Row(
                                Modifier.fillMaxWidth()
                                    .afGlow(shape, if (selected) AfAccent else AfBorder, if (selected) 1.6f else 0.6f)
                                    .clip(shape)
                                    .background(if (selected) AfAccent.copy(alpha = 0.12f) else AfNavy.copy(alpha = 0.35f))
                                    .afClick { AfThemeState.chooseTheme(t.id) }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                                        .background(Brush.linearGradient(listOf(t.accent, t.accent2)))
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(t.name, color = AfText, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                    Text("Degradê", color = t.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    AfOutlineButton("Fechar", Modifier.fillMaxWidth(), null, onClose)
                }

                // ----- direita: prévia + ajustes
                Column(
                    Modifier.weight(1.6f).fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val cur = AfThemeState.current
                    AfPanel(Modifier.fillMaxWidth(), radius = 20.dp) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            // mini prévia da tela inicial
                            Column(
                                Modifier.weight(1f).height(110.dp)
                                    .clip(AfCutShape(10.dp))
                                    .background(cur.base.copy(alpha = 0.9f))
                                    .padding(6.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(AfAccent.copy(alpha = 0.7f)))
                                Box(
                                    Modifier.fillMaxWidth().weight(1f).clip(AfCutShape(6.dp))
                                        .background(Brush.horizontalGradient(listOf(cur.accent.copy(alpha = 0.35f), cur.accent2.copy(alpha = 0.15f))))
                                ) {
                                    Box(
                                        Modifier.align(Alignment.BottomEnd).padding(5.dp).width(34.dp).height(10.dp)
                                            .clip(CircleShape).background(AfAccent)
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.height(26.dp)) {
                                    repeat(3) {
                                        Box(Modifier.weight(1f).fillMaxHeight().clip(AfCutShape(5.dp)).background(AfNavy.copy(alpha = 0.8f)))
                                    }
                                }
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("PRÉVIA DA TELA INICIAL", color = AfAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Text(cur.name, color = AfText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(cur.accent, cur.accent2, cur.border, cur.base).forEach {
                                        Box(Modifier.size(16.dp).clip(CircleShape).background(it))
                                    }
                                }
                                Box(
                                    Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                                        .background(Brush.horizontalGradient(listOf(cur.accent, cur.accent2)))
                                )
                                Text("●  Tema em uso", color = AfAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AfButton("Aplicar tema", Modifier.weight(1f), null, onClose)
                        AfOutlineButton("Zerar ajustes", Modifier.weight(1f)) { AfThemeState.resetAdjustments() }
                    }

                    AfPanel(Modifier.fillMaxWidth(), radius = 20.dp) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("AJUSTES DE COR", color = AfAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            StudioSlider("Vibração", AfThemeState.vibrance, -100, 100, true) { AfThemeState.changeVibrance(it) }
                            StudioSlider("Contraste", AfThemeState.contrast, -100, 100, true) { AfThemeState.changeContrast(it) }
                            StudioSlider("Opacidade da interface", AfThemeState.panelOpacity, 0, 100, false) { AfThemeState.changePanelOpacity(it) }
                        }
                    }

                    AfPanel(Modifier.fillMaxWidth(), radius = 20.dp) {
                        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("DEGRADÊ", color = AfAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text("Animar degradê", color = AfText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text("Faz o degradê dos botões e destaques se mover continuamente.", color = AfDim, fontSize = 12.sp)
                                }
                                Switch(
                                    checked = AfThemeState.animate,
                                    onCheckedChange = { AfThemeState.changeAnimate(it) },
                                    colors = SwitchDefaults.colors(checkedThumbColor = AfNavy, checkedTrackColor = AfAccent)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StudioSlider(label: String, value: Int, min: Int, max: Int, signed: Boolean, onChange: (Int) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = AfText, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text((if (signed && value > 0) "+" else "") + "$value%", color = AfAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.roundToInt()) },
            valueRange = min.toFloat()..max.toFloat(),
            colors = SliderDefaults.colors(thumbColor = AfAccent, activeTrackColor = AfAccent, inactiveTrackColor = AfBorder.copy(alpha = 0.35f))
        )
    }
}
