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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoyoomc.aflecklauncher.R

/** Menu lateral "Nova": trilho fino só com ícones, indicador de luz e nome pequeno embaixo. */
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
    Box(
        modifier = modifier.width(84.dp).fillMaxHeight().afHudFrame(backing = 0.30f)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(vertical = 10.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavItem(R.drawable.ic_home_filled, "Início", inHome, onHome)
            NavItem(R.drawable.ic_sports_esports_outlined, "Versões", inVersions, onVersions)
            NavItem(R.drawable.ic_download_2_filled, "Download", inDownload, onDownload)
            NavItem(R.drawable.ic_person_filled, "Contas", inAccounts, onAccounts)
            NavItem(R.drawable.ic_settings_filled, "Ajustes", inSettings || inAppearance, onSettings)
        }
    }
}

@Composable
private fun NavItem(icon: Int, label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().afClick(onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.width(2.dp).height(44.dp).background(if (selected) AfAccent else Color.Transparent))
        Column(
            modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                .background(if (selected) AfAccent.copy(alpha = 0.12f) else Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(painterResource(icon), null, tint = if (selected) AfAccent else AfDim, modifier = Modifier.size(22.dp))
            Spacer(Modifier.height(2.dp))
            Text(
                label.uppercase(), color = if (selected) AfText else AfDim,
                fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1, letterSpacing = 1.sp,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}
