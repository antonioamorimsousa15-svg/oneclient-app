package com.shoyoomc.aflecklauncher.ui.screens.main

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shoyoomc.aflecklauncher.game.renderer.Renderers
import com.shoyoomc.aflecklauncher.setting.AllSettings
import kotlinx.coroutines.delay

/** Leituras reais do aparelho (leves, a cada 2 s): RAM, CPU do launcher, armazenamento e temperatura. */
@Composable
internal fun AfTelemetry(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    var ram by remember { mutableFloatStateOf(0f) }
    var ramText by remember { mutableStateOf("--") }
    var cpu by remember { mutableFloatStateOf(0f) }
    var disk by remember { mutableFloatStateOf(0f) }
    var diskText by remember { mutableStateOf("--") }
    var temp by remember { mutableFloatStateOf(0f) }
    var tempText by remember { mutableStateOf("--") }

    LaunchedEffect(Unit) {
        var lastCpu = android.os.Process.getElapsedCpuTime()
        var lastWall = SystemClock.elapsedRealtime()
        val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        while (true) {
            runCatching {
                val am = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val mi = ActivityManager.MemoryInfo()
                am.getMemoryInfo(mi)
                val used = (mi.totalMem - mi.availMem).toFloat()
                ram = (used / mi.totalMem).coerceIn(0f, 1f)
                ramText = "%.1f / %.1f GB".format(used / 1e9f, mi.totalMem / 1e9f)

                val nowCpu = android.os.Process.getElapsedCpuTime()
                val nowWall = SystemClock.elapsedRealtime()
                val dw = (nowWall - lastWall).coerceAtLeast(1)
                cpu = ((nowCpu - lastCpu).toFloat() / dw / cores).coerceIn(0f, 1f)
                lastCpu = nowCpu; lastWall = nowWall

                val st = StatFs(Environment.getDataDirectory().path)
                val total = st.totalBytes.toFloat()
                val free = st.availableBytes.toFloat()
                disk = ((total - free) / total).coerceIn(0f, 1f)
                diskText = "%.0f GB livres".format(free / 1e9f)

                val bi: Intent? = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                val t = (bi?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f
                temp = (t / 60f).coerceIn(0f, 1f)
                tempText = "%.1f °C".format(t)
            }
            delay(2000)
        }
    }

    val rendererId = AllSettings.renderer.state
    val rendererName = Renderers.getRenderers().firstOrNull { it.getUniqueIdentifier() == rendererId }?.getRendererName() ?: "automático"

    Column(modifier, verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(9.dp)) {
        TelemetryRow("MEMÓRIA", ram, ramText)
        TelemetryRow("CPU LAUNCHER", cpu, "%d%%".format((cpu * 100).toInt()))
        TelemetryRow("ARMAZENAM.", disk, diskText)
        TelemetryRow("TEMPERATURA", temp, tempText)
        Box(Modifier.fillMaxWidth().height(1.dp).background(AfBorder.copy(alpha = 0.3f)))
        Row(Modifier.fillMaxWidth()) {
            Text("RENDER", color = AfDim, fontSize = 10.sp, letterSpacing = 1.5.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.4f))
            Text(rendererName.uppercase(), color = AfAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, maxLines = 1, modifier = Modifier.weight(0.6f))
        }
    }
}

@Composable
private fun TelemetryRow(label: String, fraction: Float, value: String) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            Text(label, color = AfDim, fontSize = 10.sp, letterSpacing = 1.5.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
            Text(value, color = AfText, fontSize = 11.sp, fontFamily = FontFamily.Monospace, maxLines = 1)
        }
        Box(Modifier.padding(top = 3.dp).fillMaxWidth().height(5.dp).background(Color.Black.copy(alpha = 0.55f))) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(fraction.coerceIn(0.02f, 1f)).background(AfAccent))
        }
    }
}
