package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoyoomc.aflecklauncher.R

/** Menu lateral: Início, Versões, Download, Contas, Ajustes. */
@Composable
fun AfSidebar(
    inHome: Boolean,
    inVersions: Boolean,
    inDownload: Boolean,
    inAccounts: Boolean,
    inSettings: Boolean,
    inAppearance: Boolean,
    onHome: () -> Unit,
    onVersions: () -> Unit,
    onDownload: () -> Unit,
    onAccounts: () -> Unit,
    onSettings: () -> Unit,
    onAppearance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val panelShape = androidx.compose.foundation.shape.RoundedCornerShape(if (AfThemeState.roundStyle) 30.dp else 5.dp)
    Box(
        modifier = modifier.width(205.dp).fillMaxHeight()
            .afHudFrame(backing = 0.14f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            NavPill(R.drawable.af_sym_home, "01", "Início", inHome, onHome)
            NavPill(R.drawable.af_sym_versions, "02", "Versões", inVersions, onVersions)
            NavPill(R.drawable.af_sym_download, "03", "Download", inDownload, onDownload)
            NavPill(R.drawable.af_sym_account, "04", "Contas", inAccounts, onAccounts)
            NavPill(R.drawable.af_sym_settings, "05", "Ajustes", inSettings, onSettings)
        }
    }
}

@Composable
private fun NavPill(icon: Int, code: String, label: String, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) AfAccent else AfBorder
    Row(
        modifier = Modifier.fillMaxWidth().height(55.dp)
            .afDarkGlass(color, selected, 28.dp)
            .afClick(onClick)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(41.dp)
                .clip(AfHexShape()).border(1.2.dp, color, AfHexShape()),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(icon), null, tint = AfText, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(code, color = if (selected) AfNavy.copy(alpha = 0.7f) else AfAccent, fontSize = 10.sp, fontFamily = AfCondensed, letterSpacing = 2.sp, maxLines = 1)
            Text(
                label.uppercase(), color = if (selected) AfNavy else AfText,
                fontSize = 17.sp, fontFamily = AfCondensed, letterSpacing = 1.sp, maxLines = 1
            )
        }
    }
}
