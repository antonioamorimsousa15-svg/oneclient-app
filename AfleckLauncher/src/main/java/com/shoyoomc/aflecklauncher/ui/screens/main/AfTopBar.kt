package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoyoomc.aflecklauncher.BuildConfig
import com.shoyoomc.aflecklauncher.R
import com.shoyoomc.aflecklauncher.game.account.AccountsManager

/** Barra de cima: marca, seção atual, Arquivos e conta. */
@Composable
fun AfTopBar(
    sectionTitle: String,
    onLogo: () -> Unit,
    onFiles: () -> Unit,
    onUser: () -> Unit,
    modifier: Modifier = Modifier
) {
    val account by AccountsManager.currentAccountFlow.collectAsState()
    val ctx = LocalContext.current
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Pill(onClick = onLogo) {
            androidx.compose.foundation.Image(
                painterResource(R.drawable.af_logo), null,
                modifier = Modifier.size(54.dp).clip(AfHexShape()),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text("AFLECK", color = AfText, fontSize = 24.sp, fontFamily = AfCondensed, letterSpacing = 3.sp, maxLines = 1)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("NEXT-GENERATION GAME LAUNCHER", color = AfDim, fontSize = 8.sp, fontFamily = AfCondensed, letterSpacing = 1.2.sp, maxLines = 1)
                    Spacer(Modifier.width(6.dp))
                    Box(Modifier.border(1.dp, AfAccent, RoundedCornerShape(10.dp)).padding(horizontal = 6.dp, vertical = 0.dp)) {
                        Text("r" + BuildConfig.VERSION_NAME.removePrefix("r") + " P101", color = AfAccent, fontSize = 9.sp, fontFamily = AfCondensed, maxLines = 1)
                    }
                }
            }
        }
        Pill {
            androidx.compose.foundation.Image(
                painterResource(R.drawable.af_logo), null,
                modifier = Modifier.size(40.dp).clip(AfHexShape()),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(Modifier.width(10.dp))
            Text(sectionTitle, color = AfText, fontSize = 22.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
        Spacer(Modifier.weight(1f))
        Pill(onClick = { AfMusic.changeShow(ctx, !AfMusic.showWidget) }) {
            AfIconCircle(R.drawable.af_sym_music, size = 40.dp, filled = AfMusic.showWidget)
        }
        Pill(onClick = onFiles) {
            AfIconCircle(R.drawable.af_sym_folder, size = 40.dp, filled = true)
            Spacer(Modifier.width(10.dp))
            Text("Arquivos", color = AfText, fontSize = 21.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
        Pill(onClick = onUser) {
            AfIconCircle(R.drawable.af_sym_account, size = 40.dp)
            Spacer(Modifier.width(10.dp))
            Text(
                account?.username ?: "Adicionar Conta", color = AfText, fontSize = 21.sp,
                fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun Pill(onClick: (() -> Unit)? = null, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.afDarkGlass(AfBorder, radius = 24.dp)
            .then(if (onClick != null) Modifier.afClick(onClick) else Modifier)
            .padding(start = 5.dp, end = 16.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
