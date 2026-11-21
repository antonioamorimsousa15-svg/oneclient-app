package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class AfTabSpec(val iconRes: Int, val textRes: Int, val selected: Boolean, val onClick: () -> Unit)

/** Barra de abas em cápsula (01 Minecraft, 02 Mods, ...): aba ativa em degradê, abas espalhadas na largura toda. */
@Composable
fun AfTabBar(tabs: List<AfTabSpec>, modifier: Modifier = Modifier) {
    val capsule = RoundedCornerShape(16.dp)
    BoxWithConstraints(
        modifier = modifier
            .afGlow(capsule, AfBorder, 0.8f)
            .clip(capsule)
            .background(AfPanelDeep)
    ) {
        val minW = maxWidth - 16.dp
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .widthIn(min = minW),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, t ->
                Row(
                    modifier = Modifier
                        .clip(capsule)
                        .then(
                            if (t.selected) Modifier.background(Brush.horizontalGradient(listOf(AfAccent, AfAccent2)))
                            else Modifier
                        )
                        .clickable { t.onClick() }
                        .padding(start = 5.dp, end = 16.dp, top = 5.dp, bottom = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (t.selected) Color.White.copy(alpha = 0.22f) else androidx.compose.ui.graphics.Color.White.copy(alpha = 0.10f))
                            .border(1.dp, if (t.selected) AfBorder else AfDim.copy(alpha = 0.6f), CircleShape),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(t.iconRes), null,
                            tint = if (t.selected) AfAccent else AfDim,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "%02d".format(index + 1),
                            color = if (t.selected) Color(0xFF0B0B0D).copy(alpha = 0.7f) else AfDim,
                            fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp
                        )
                        Text(
                            stringResource(t.textRes),
                            color = if (t.selected) Color(0xFF0B0B0D) else AfText,
                            fontSize = 17.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
