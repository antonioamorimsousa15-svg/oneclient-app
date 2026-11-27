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
    val panelShape = androidx.compose.foundation.shape.RoundedCornerShape(if (AfThemeState.roundStyle) 20.dp else 8.dp)
    Box(
        modifier = modifier.width(188.dp).fillMaxHeight()
            .afHudFrame(backing = 0.14f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            NavPill(R.drawable.af_sym_home, "Início", inHome, onHome)
            NavPill(R.drawable.af_sym_versions, "Versões", inVersions, onVersions)
            NavPill(R.drawable.af_sym_download, "Downloads", inDownload, onDownload)
            NavPill(R.drawable.af_sym_account, "Contas", inAccounts, onAccounts)
            NavPill(R.drawable.af_sym_settings, "Ajustes", inSettings, onSettings)
        }
    }
}

@Composable
private fun NavPill(icon: Int, label: String, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) AfAccent else AfBorder
    Row(
        modifier = Modifier.fillMaxWidth().height(52.dp)
            .afDarkGlass(color, selected, 18.dp)
            .afClick(onClick)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(38.dp)
                .clip(AfHexShape()).border(1.2.dp, color, AfHexShape()),
            contentAlignment = Alignment.Center
        ) {
            Icon(painterResource(icon), null, tint = AfText, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(
            label, color = if (selected) AfNavy else AfText,
            fontSize = 16.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            maxLines = 1
        )
    }
}
