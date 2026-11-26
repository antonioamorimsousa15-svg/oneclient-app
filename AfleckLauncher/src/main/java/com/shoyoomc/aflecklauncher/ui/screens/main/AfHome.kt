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

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ---------- Linha de cima: Bem-vindo + Versão do Minecraft ----------
        Row(Modifier.fillMaxWidth().height(250.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Bem-vindo de volta
            AfPanel(Modifier.weight(1.65f).fillMaxHeight()) {
                AfCardBg(homeSlot)
                Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Bem-vindo de volta,", color = AfText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "Minecraft Java", color = AfText, fontSize = 44.sp, fontWeight = FontWeight.Bold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text("Sua nova jornada começa aqui!", color = AfText, fontSize = 18.sp)
                    if (current != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            HomeChip(current?.getVersionName()?.uppercase() ?: "")
                            if (loaderName.isNotEmpty()) HomeChip(loaderName.uppercase())
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (current != null) {
                            AfButton("Jogar Agora", Modifier.width(190.dp), R.drawable.af_sym_play) { current?.let(onPlay) }
                        } else {
                            AfButton("BAIXAR MINECRAFT", Modifier.width(220.dp), R.drawable.af_sym_download, onOpenDownload)
                        }
                        Box(Modifier.afClick { onEdit("home") }) { AfIconCircle(R.drawable.ic_image_filled, size = 50.dp) }
                    }
                }
            }
            // Versão do Minecraft
            AfPanel(Modifier.weight(1.0f).fillMaxHeight()) {
                Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.af_sym_versions), null, tint = AfText, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Versão do Minecraft", color = AfText, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1, modifier = Modifier.weight(1f))
                    }
                    Row(Modifier.weight(1f).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        VersionIconImage(current, Modifier.size(52.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(current?.getVersionName() ?: "Nenhuma", color = AfText, fontSize = 26.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                if (current != null) summary.replace(", ", " • ").substringBefore(" -") else "Escolha uma versão",
                                color = AfDim, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (current != null) AfButton("JOGAR", Modifier.weight(1f), R.drawable.af_sym_play) { current?.let(onPlay) }
                        else AfButton("ESCOLHER", Modifier.weight(1f), null, onOpenVersions)
                        Box(Modifier.afClick(onOpenVersions)) { AfIconCircle(R.drawable.af_sym_settings, size = 44.dp) }
                    }
                }
            }
        }
        // ---------- Meio: Suas versões + Conta + Texturas/Shaders, com a imagem 9:16 ao lado ----------
        Row(Modifier.fillMaxWidth().height(440.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(Modifier.weight(2.79f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(Modifier.fillMaxWidth().height(228.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Suas versões
            AfPanel(Modifier.weight(1.26f).fillMaxHeight()) {
                AfCardBg(versSlot)
                Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_package_2_outlined), null, tint = AfText, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Suas versões", color = AfText, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Ver todas ›", color = AfAccent, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.afClick(onOpenVersions))
                        Spacer(Modifier.width(6.dp))
                        Box(Modifier.afClick { onEdit("vers") }) { AfIconCircle(R.drawable.ic_image_filled, size = 30.dp) }
                    }
                    val first = allVersions.firstOrNull()
                    if (first == null) {
                        Text("Nenhuma versão instalada.", color = AfDim, fontSize = 16.sp)
                        Spacer(Modifier.weight(1f))
                        AfButton("Baixar", Modifier.fillMaxWidth(), null, onOpenDownload)
                    } else {
                        allVersions.take(2).forEach { v ->
                            Row(
                                Modifier.fillMaxWidth().afRect(AfBorder, radius = 16.dp).padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VersionIconImage(v, Modifier.size(36.dp))
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(v.getVersionName(), color = AfText, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(runCatching { v.getVersionSummary().replace(", ", " • ").substringBefore(" -") }.getOrDefault(""), color = AfDim, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                Box(Modifier.afClick { onPlay(v) }) { AfIconCircle(R.drawable.af_sym_play, size = 34.dp) }
                            }
                        }
                    }
                }
            }
            // Conta
            AfPanel(Modifier.weight(1.0f).fillMaxHeight()) {
                Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.af_sym_account), null, tint = AfText, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Conta", color = AfText, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Row(
                            Modifier.clip(CircleShape).background(AfAccent).afClick(onOpenAccounts)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(painterResource(R.drawable.ic_checkroom), null, tint = AfNavy, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Skins", color = AfNavy, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(58.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f)).border(2.dp, AfAccent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val skin = account?.takeIf { it.hasSkinFile }?.getSkinFile()
                            if (skin != null) {
                                HomeSkinHead(skin, Modifier.fillMaxSize().padding(5.dp))
                            } else if (userName == null) {
                                Icon(painterResource(R.drawable.ic_add), null, tint = AfText, modifier = Modifier.size(28.dp))
                            } else {
                                Text(userName.take(1).uppercase(), color = AfAccent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                userName ?: "Adicionar Conta", color = AfText, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                                maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(if (userName == null) AfDim else Color(0xFF4ADE80)))
                                Spacer(Modifier.width(6.dp))
                                Text(accountKind, color = if (userName == null) AfDim else Color(0xFF4ADE80), fontSize = 16.sp)
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    AfOutlineButton("Gerenciar Conta", Modifier.fillMaxWidth(), R.drawable.af_sym_settings, onOpenAccounts)
                }
            }
        }
        Row(Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            HomeShortcutCard(Modifier.weight(1f), R.drawable.ic_styler, "Texturas", "Mude o visual do jogo com texturas novas.", "Ver texturas ›", onOpenVersions)
            HomeShortcutCard(Modifier.weight(1f), R.drawable.ic_lightbulb, "Shaders", "Deixe seu mundo mais bonito com shaders.", "Explorar ›", onOpenVersions)
        }
        }
            // Imagem 9:16 (plano de fundo)
            Box(
                Modifier.weight(1.0f).fillMaxHeight().clip(androidx.compose.foundation.shape.RoundedCornerShape(26.dp))
                    .border(1.5.dp, AfBorder, androidx.compose.foundation.shape.RoundedCornerShape(26.dp))
            ) {
                AfSlotImage(slot916, R.drawable.af_bg, Modifier.fillMaxSize())
                Box(Modifier.align(Alignment.TopEnd).padding(8.dp).afClick { onEdit("card916") }) {
                    AfIconCircle(R.drawable.ic_image_filled, size = 40.dp)
                }
                Text(
                    "9:16", color = AfText, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomStart).padding(10.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)).padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        // ---------- Linha de baixo: Texturas, Shaders, Configurações Rápidas ----------
        Row(Modifier.fillMaxWidth().height(231.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AfPanel(Modifier.weight(1.0f).fillMaxHeight()) {
                Column(Modifier.fillMaxSize().padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_star_filled), null, tint = AfAccent, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Funções Extras", color = AfText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        Modifier.fillMaxWidth().height(72.dp).afRect(AfBorder, radius = 14.dp).afClick(onOpenSettings).padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AfIconCircle(R.drawable.ic_star_filled, size = 44.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Novidades", color = AfText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Text("Veja o que há de novo no Afleck", color = AfAccent, fontSize = 15.sp)
                        }
                        Icon(painterResource(R.drawable.ic_keyboard_arrow_right), null, tint = AfDim, modifier = Modifier.size(24.dp))
                    }
                    Row(
                        Modifier.fillMaxWidth().height(52.dp).afRect(AfBorder, radius = 14.dp).padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painterResource(R.drawable.ic_add), null, tint = AfDim, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("Mais funções em breve", color = AfDim, fontSize = 17.sp)
                    }
                }
            }
            AfPanel(Modifier.weight(1.0f).fillMaxHeight()) {
                AfCardBg(quickSlot)
                Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.af_sym_settings), null, tint = AfText, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Configurações Rápidas", color = AfText, fontSize = 21.sp, fontWeight = FontWeight.Bold, maxLines = 1, modifier = Modifier.weight(1f))
                        Box(Modifier.afClick { onEdit("shortcuts") }) { AfIconCircle(R.drawable.ic_edit_outlined, size = 34.dp) }
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
                        Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        modifier = modifier.fillMaxHeight().afRect(AfAccent, radius = 14.dp).afClick(onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(painterResource(icon), null, tint = AfAccent, modifier = Modifier.size(22.dp))
        Text(label, color = AfText, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, textAlign = TextAlign.Center)
    }
}
