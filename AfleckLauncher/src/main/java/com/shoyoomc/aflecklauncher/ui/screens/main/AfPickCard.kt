package com.shoyoomc.aflecklauncher.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Card de escolha (bolinha de seleção + título + resumo), usado em listas de 2 colunas. */
@Composable
internal fun AfPickCard(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    summary: (@Composable () -> Unit)? = null
) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (selected) AfAccent.copy(alpha = 0.14f) else AfNavy.copy(alpha = 0.35f))
            .border(if (selected) 2.dp else 1.dp, if (selected) AfAccent else AfBorder.copy(alpha = 0.5f), shape)
            .afClick(onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(30.dp).clip(CircleShape)
                .background(if (selected) AfAccent else Color.Transparent)
                .border(2.dp, AfAccent.copy(alpha = 0.85f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (selected) Text("✓", color = AfNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = AfText, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides AfDim
            ) { summary?.invoke() }
        }
    }
}

/** Distribui [items] em linhas de 2 colunas alinhadas. */
@Composable
internal fun <T> AfPickGrid(
    items: List<T>,
    modifier: Modifier = Modifier,
    card: @Composable (item: T, cardModifier: Modifier) -> Unit
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { item -> card(item, Modifier.weight(1f)) }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
