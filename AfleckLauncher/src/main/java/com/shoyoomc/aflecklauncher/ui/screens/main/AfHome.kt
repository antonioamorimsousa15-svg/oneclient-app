package com.shoyoomc.aflecklauncher.ui.screens.main

import com.shoyoomc.aflecklauncher.ui.screens.content.elements.VersionIconImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import com.shoyoomc.aflecklauncher.game.account.isLocalAccount
import com.shoyoomc.aflecklauncher.game.account.isMicrosoftAccount
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoyoomc.aflecklauncher.R
import com.shoyoomc.aflecklauncher.game.account.AccountsManager
import com.shoyoomc.aflecklauncher.game.version.installed.Version
import com.shoyoomc.aflecklauncher.game.version.installed.VersionsManager

/** Tela Início do Afleck, igual ao esqueleto desenhado pelo usuário. */
@Composable
fun AfHome(
    onPlay: (Version) -> Unit,
    onOpenVersions: () -> Unit,
    onOpenAccounts: () -> Unit,
    onOpenDownload: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAppearance: () -> Unit,
) {
    val slot916 = rememberAfSlot("card916")
    val homeSlot = rememberAfSlot("home")
    val accSlot = rememberAfSlot("acc")
    val versSlot = rememberAfSlot("vers")
    val mcvSlot = rememberAfSlot("mcv")
    val quickSlot = rememberAfSlot("quick")
    var dialog by remember { mutableStateOf<String?>(null) }

    Box(Modifier.fillMaxSize()) {
        AfHomeContent(
            onPlay, onOpenVersions, onOpenAccounts, onOpenDownload, onOpenSettings, onOpenAppearance,
            slot916, homeSlot, accSlot, versSlot, mcvSlot, quickSlot, editing = dialog, onEdit = { dialog = it.ifEmpty { null } }
        )
    }
    when (dialog) {
        "home" -> AfCardDialog("Fundo de Bem-vindo", homeSlot) { dialog = null }
        "acc" -> AfCardDialog("Fundo de Conta", accSlot) { dialog = null }
        "vers" -> AfCardDialog("Fundo de Suas versões", versSlot) { dialog = null }
        "mcv" -> AfCardDialog("Fundo de Versão do Minecraft", mcvSlot) { dialog = null }
        "quick" -> AfCardDialog("Fundo de Configurações Rápidas", quickSlot) { dialog = null }
        "card916" -> AfCardDialog("Imagem 9:16", slot916) { dialog = null }
    }
}

@Composable
private fun AfCardDialog(title: String, slot: AfImageSlot, onDismiss: () -> Unit) {
    AfSlotDialog(title, "Imagem, GIF ou vídeo para o fundo deste quadro. A opacidade daqui é separada das outras.", slot, onDismiss)
}

@Composable
private fun AfHomeContent(
    onPlay: (Version) -> Unit,
    onOpenVersions: () -> Unit,
    onOpenAccounts: () -> Unit,
    onOpenDownload: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAppearance: () -> Unit,
    slot916: AfImageSlot,
    homeSlot: AfImageSlot,
    accSlot: AfImageSlot,
    versSlot: AfImageSlot,
    mcvSlot: AfImageSlot,
    quickSlot: AfImageSlot,
    editing: String?,
    onEdit: (String) -> Unit,
) {
    val account by AccountsManager.currentAccountFlow.collectAsState()
    val current by VersionsManager.currentVersion.collectAsState()
    val allVersions by VersionsManager.versions.collectAsState()
    val userName = account?.username
    val editShortcuts = editing == "shortcuts"

    val summary = current?.let { runCatching { it.getVersionSummary() }.getOrDefault("") } ?: ""
    val loaderName = summary.substringAfter(", ", "").substringBefore(" -").trim()
    val accountKind = when {
        account == null -> "Nenhuma conta"
        account?.isMicrosoftAccount() == true -> "Conta Microsoft"
        account?.isLocalAccount() == true -> "Conta offline"
        else -> "Conta"
    }

    val mono = androidx.compose.ui.text.font.FontFamily.Monospace
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(Modifier.fillMaxWidth().height(290.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // ---------- SESSÃO ----------
            AfPanel(Modifier.weight(1.5f).fillMaxHeight()) {
                Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("// SESSÃO", color = AfDim, fontSize = 11.sp, letterSpacing = 3.sp, fontFamily = mono)
                    Text(
                        current?.getVersionName() ?: "SEM VERSÃO", color = AfText, fontSize = 34.sp, fontWeight = FontWeight.Bold,
                        fontFamily = mono, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        if (current != null) ("[" + (if (loaderName.isEmpty()) "VANILLA" else loaderName.uppercase()) + "]  " + summary.substringBefore(",")) else "Instale uma versão para começar",
                        color = AfAccent, fontSize = 13.sp, fontFamily = mono, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Box(Modifier.padding(vertical = 6.dp).fillMaxWidth().height(1.dp).background(AfBorder.copy(alpha = 0.3f)))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.afClick(onOpenAccounts)) {
                        Box(
                            Modifier.size(46.dp).clip(RoundedCornerShape(3.dp)).background(Color.Black.copy(alpha = 0.5f)).border(1.dp, AfAccent.copy(alpha = 0.6f), RoundedCornerShape(3.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            val skin = account?.takeIf { it.hasSkinFile }?.getSkinFile()
                            if (skin != null) HomeSkinHead(skin, Modifier.fillMaxSize().padding(3.dp))
                            else Icon(painterResource(R.drawable.ic_person_filled), null, tint = AfDim, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(userName ?: "SEM CONTA", color = AfText, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = mono, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(accountKind.uppercase(), color = AfDim, fontSize = 11.sp, letterSpacing = 1.sp, fontFamily = mono)
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    if (current != null) AfButton("Iniciar jogo", Modifier.fillMaxWidth(), R.drawable.ic_play_arrow_filled) { current?.let(onPlay) }
                    else AfButton("Baixar Minecraft", Modifier.fillMaxWidth(), R.drawable.ic_download_2_filled, onOpenDownload)
                }
            }
            // ---------- TELEMETRIA ----------
            AfPanel(Modifier.weight(1f).fillMaxHeight()) {
                Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("// TELEMETRIA", color = AfDim, fontSize = 11.sp, letterSpacing = 3.sp, fontFamily = mono)
                    AfTelemetry(Modifier.fillMaxWidth())
                }
            }
        }
        Row(Modifier.fillMaxWidth().height(190.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            // ---------- VERSÕES ----------
            AfPanel(Modifier.weight(1.5f).fillMaxHeight()) {
                Column(Modifier.fillMaxSize().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("// VERSÕES", color = AfDim, fontSize = 11.sp, letterSpacing = 3.sp, fontFamily = mono, modifier = Modifier.weight(1f))
                        Text("VER TODAS >", color = AfAccent, fontSize = 11.sp, letterSpacing = 1.sp, fontFamily = mono, modifier = Modifier.afClick(onOpenVersions))
                    }
                    if (allVersions.isEmpty()) {
                        Spacer(Modifier.weight(1f))
                        Text("NENHUMA VERSÃO INSTALADA", color = AfDim, fontSize = 12.sp, fontFamily = mono)
                        Spacer(Modifier.weight(1f))
                    } else {
                        allVersions.take(3).forEach { v ->
                            Box(Modifier.padding(top = 6.dp).fillMaxWidth().height(1.dp).background(AfBorder.copy(alpha = 0.2f)))
                            Row(Modifier.weight(1f).fillMaxWidth().afClick { onPlay(v) }, verticalAlignment = Alignment.CenterVertically) {
                                Text(v.getVersionName(), color = AfText, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = mono, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                Text(runCatching { v.getVersionSummary().substringBefore(" -").replace(", ", " / ") }.getOrDefault(""), color = AfDim, fontSize = 11.sp, fontFamily = mono, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                Icon(painterResource(R.drawable.ic_play_arrow_filled), null, tint = AfAccent, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
            // ---------- ATALHOS ----------
            AfPanel(Modifier.weight(1f).fillMaxHeight()) {
                Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("// ATALHOS", color = AfDim, fontSize = 11.sp, letterSpacing = 3.sp, fontFamily = mono, modifier = Modifier.weight(1f))
                        Text("EDITAR", color = AfAccent, fontSize = 11.sp, letterSpacing = 1.sp, fontFamily = mono, modifier = Modifier.afClick { onEdit("shortcuts") })
                    }
                    val ctx = androidx.compose.ui.platform.LocalContext.current
                    val picked = remember { mutableStateOf(afLoadShortcuts(ctx)) }
                    val actions = mapOf(
                        "graficos" to onOpenSettings, "hud" to onOpenSettings, "aparencia" to onOpenAppearance,
                        "contas" to onOpenAccounts, "versoes" to onOpenVersions, "download" to onOpenDownload,
                        "texturas" to onOpenVersions, "shaders" to onOpenVersions, "config" to onOpenSettings
                    )
                    val cols = if (picked.value.size > 4) 3 else 2
                    picked.value.mapNotNull { id -> AF_SHORTCUTS.firstOrNull { it.id == id } }.chunked(cols).forEach { rowItems ->
                        Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            rowItems.forEach { sc -> QuickTile(Modifier.weight(1f), sc.icon, sc.label, actions[sc.id] ?: onOpenSettings) }
                            repeat(cols - rowItems.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                    if (editShortcuts) {
                        AfShortcutsDialog(picked.value, onSave = { picked.value = it; afSaveShortcuts(ctx, it) }) { onEdit("") }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeChip(text: String) {
    Row(
        modifier = Modifier.afRect(AfAccent, radius = 50.dp).padding(horizontal = 12.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(AfAccent))
        Spacer(Modifier.width(6.dp))
        Text(text, color = AfAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun HomeShortcutCard(modifier: Modifier, icon: Int, title: String, subtitle: String, buttonText: String, onClick: () -> Unit) {
    AfPanel(modifier.fillMaxHeight()) {
        Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(icon), null, tint = AfText, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text(title, color = AfText, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Box(Modifier.afClick(onClick)) { AfIconCircle(R.drawable.ic_edit_outlined, size = 34.dp) }
            }
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    Modifier.weight(0.9f).fillMaxHeight().afRect(AfBorder, radius = 14.dp),
                    contentAlignment = Alignment.Center
                ) { Icon(painterResource(icon), null, tint = AfAccent, modifier = Modifier.size(44.dp)) }
                Column(Modifier.weight(1.3f).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                    Text(subtitle, color = AfText, fontSize = 17.sp)
                    AfButton(buttonText, Modifier.fillMaxWidth().height(40.dp), onClick = onClick)
                }
            }
        }
    }
}

/** Cabeça (rosto + camada externa) da skin, sem suavização. */
@Composable
private fun HomeSkinHead(file: java.io.File, modifier: Modifier = Modifier) {
    val bmp by androidx.compose.runtime.produceState<androidx.compose.ui.graphics.ImageBitmap?>(null, file) {
        value = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            runCatching { android.graphics.BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap() }.getOrNull()
        }
    }
    val img = bmp
    androidx.compose.foundation.Canvas(modifier) {
        if (img != null && img.width >= 64 && img.height >= 16) {
            val dst = androidx.compose.ui.unit.IntSize(size.width.toInt(), size.height.toInt())
            drawImage(img, srcOffset = androidx.compose.ui.unit.IntOffset(8, 8), srcSize = androidx.compose.ui.unit.IntSize(8, 8), dstSize = dst, filterQuality = androidx.compose.ui.graphics.FilterQuality.None)
            drawImage(img, srcOffset = androidx.compose.ui.unit.IntOffset(40, 8), srcSize = androidx.compose.ui.unit.IntSize(8, 8), dstSize = dst, filterQuality = androidx.compose.ui.graphics.FilterQuality.None)
        }
    }
}

@Composable
private fun QuickTile(modifier: Modifier, icon: Int, label: String, onClick: () -> Unit) {
    Column(
        modifier = modifier.fillMaxHeight().afRect(AfAccent, radius = 4.dp).afClick(onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(painterResource(icon), null, tint = AfAccent, modifier = Modifier.size(22.dp))
        Text(label.uppercase(), color = AfText, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1, letterSpacing = 0.5.sp, textAlign = TextAlign.Center, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
    }
}
