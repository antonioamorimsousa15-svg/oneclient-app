/*
 * Zalith Launcher 2
 * Copyright (C) 2025-2026 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.shoyoomc.aflecklauncher.ui.screens.game.elements

import android.widget.Toast
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.shoyoomc.aflecklauncher.ui.screens.main.AfAccent
import com.shoyoomc.aflecklauncher.ui.screens.main.AfCutShape
import com.shoyoomc.aflecklauncher.ui.screens.main.AfIconCircle
import com.shoyoomc.aflecklauncher.ui.screens.main.AfNavy
import com.shoyoomc.aflecklauncher.ui.screens.main.AfText
import com.shoyoomc.aflecklauncher.ui.screens.main.AfMusicSettings
import com.shoyoomc.aflecklauncher.ui.screens.main.AfMusicWidget
import com.shoyoomc.aflecklauncher.ui.screens.main.afClick
import com.shoyoomc.aflecklauncher.ui.screens.main.afGlow
import com.shoyoomc.aflecklauncher.ui.screens.main.afHud
import com.shoyoomc.aflecklauncher.ui.screens.main.afHudFrame
import com.shoyoomc.aflecklauncher.ui.screens.main.afHudScreen
import kotlinx.coroutines.delay
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shoyoomc.aflecklauncher.R
import com.shoyoomc.aflecklauncher.game.sdl.SdlBridge
import com.shoyoomc.aflecklauncher.setting.AllSettings
import com.shoyoomc.aflecklauncher.setting.enums.FpsDisplayMode
import com.shoyoomc.aflecklauncher.setting.enums.GamepadInputMode
import com.shoyoomc.aflecklauncher.setting.enums.GestureActionType
import com.shoyoomc.aflecklauncher.setting.enums.MouseControlMode
import com.shoyoomc.aflecklauncher.setting.enums.MouseTouchBlockMode
import com.shoyoomc.aflecklauncher.setting.enums.ResolutionRule
import com.shoyoomc.aflecklauncher.setting.unit.floatRange
import com.shoyoomc.aflecklauncher.ui.AndroidStringText
import com.shoyoomc.aflecklauncher.ui.androidText
import com.shoyoomc.aflecklauncher.ui.components.BackgroundCard
import com.shoyoomc.aflecklauncher.ui.components.DualMenuSubscreen
import com.shoyoomc.aflecklauncher.ui.components.IntInputField
import com.shoyoomc.aflecklauncher.ui.components.MenuListLayout
import com.shoyoomc.aflecklauncher.ui.components.MenuSliderLayout
import com.shoyoomc.aflecklauncher.ui.components.MenuState
import com.shoyoomc.aflecklauncher.ui.components.MenuSwitchButton
import com.shoyoomc.aflecklauncher.ui.components.MenuTextButton
import com.shoyoomc.aflecklauncher.ui.components.lazyScrollWithBar
import com.shoyoomc.aflecklauncher.ui.control.HotbarRule
import com.shoyoomc.aflecklauncher.ui.control.gyroscope.isGyroscopeAvailable
import com.shoyoomc.aflecklauncher.ui.theme.cardColor
import com.shoyoomc.aflecklauncher.ui.theme.cardTitleColor
import com.shoyoomc.aflecklauncher.ui.theme.onCardColor
import com.shoyoomc.aflecklauncher.utils.animation.getAnimateTween
import com.shoyoomc.aflecklauncher.utils.customResolutionRange
import com.shoyoomc.aflecklauncher.utils.ensureCustomResolutionInitialized
import com.shoyoomc.aflecklauncher.utils.getRealScreenSize
import com.shoyoomc.aflecklauncher.viewmodel.GamepadViewModel

private data class IconTab(val iconRes: Int, val iconSize: Dp = 18.dp)

private val controlTabs = listOf(
    //概览
    IconTab(R.drawable.ic_dashboard_filled),
    //虚拟鼠标设置
    IconTab(R.drawable.ic_mouse_filled, iconSize = 16.dp),
    //手柄设置
    IconTab(R.drawable.ic_sports_esports_filled),
    //手势控制设置
    IconTab(R.drawable.ic_touch_app_filled),
    //陀螺仪设置
    IconTab(R.drawable.ic_mobile_rotate_filled)
)

private enum class DockPanel(val title: String) {
    Controls("CONTROLES"), Gestures("GESTOS"), Mouse("MOUSE"), Gamepad("CONTROLE / GAMEPAD"),
    Gyro("GIROSCÓPIO"), Resolution("RESOLUÇÃO E TELA"), Game("MENU DO JOGO"), Music("MÚSICA")
}

@Composable
fun GameMenuSubscreen(
    state: MenuState,
    controlMenuTabIndex: Int,
    onControlMenuTabChange: (Int) -> Unit,
    gamepadViewModel: GamepadViewModel,
    closeScreen: () -> Unit,
    onForceClose: () -> Unit,
    onSwitchLog: () -> Unit,
    enableTerracotta: Boolean,
    onOpenTerracottaMenu: () -> Unit,
    onRefreshWindowSize: () -> Unit,
    onInputMethod: () -> Unit,
    onSendKeycode: () -> Unit,
    onReplacementControl: () -> Unit,
    onEditLayout: () -> Unit,
    onShowToast: (AndroidStringText, Int) -> Unit
) {
    val visible = state == MenuState.SHOW
    var panel by remember { mutableStateOf<DockPanel?>(null) }
    LaunchedEffect(visible) { if (!visible) panel = null }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val panelHeight = (maxHeight - 210.dp).coerceAtLeast(170.dp)

        AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = closeScreen
                    )
            )
        }

        AfMusicWidget(Modifier.align(Alignment.TopCenter), showCapsule = !(visible && panel == null))

        AnimatedVisibility(
            modifier = Modifier.align(Alignment.TopCenter),
            visible = visible && panel == null,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it }
        ) {
            Column(
                modifier = Modifier.padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AfGameDock(
                    active = panel,
                    onOpen = { target -> panel = if (panel == target) null else target },
                    onKeyboard = { onInputMethod() },
                    onLog = { onSwitchLog(); closeScreen() },
                    onForceClose = onForceClose
                )

            }
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.Center),
            visible = visible && panel != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val current = panel ?: return@AnimatedVisibility
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.74f)
                    .fillMaxHeight(0.82f)
                    .afHudScreen(backing = 0.72f, glow = true)
                    .afTechMotion()
                    .afTechCorners(len = 18.dp, inset = 4.dp)
                    .padding(6.dp)
            ) {
                        AfTechHeader(
                            code = "MOD-" + "%02d".format(current.ordinal + 1),
                            title = current.title,
                            onClose = { panel = null }
                        )
                        AfTechHint(dockHint(current))
                        Box(modifier = Modifier.weight(1f)) {
                            when (current) {
                                DockPanel.Controls -> ControlOverview(
                                    modifier = Modifier.fillMaxSize(),
                                    closeScreen = closeScreen,
                                    onInputMethod = onInputMethod,
                                    onSendKeycode = onSendKeycode,
                                    onReplacementControl = onReplacementControl,
                                    onEditLayout = onEditLayout
                                )
                                DockPanel.Gestures -> ControlGesture(modifier = Modifier.fillMaxSize())
                                DockPanel.Mouse -> ControlMouse(modifier = Modifier.fillMaxSize())
                                DockPanel.Gamepad -> ControlGamepad(
                                    modifier = Modifier.fillMaxSize(),
                                    gamepadViewModel = gamepadViewModel
                                )
                                DockPanel.Gyro -> ControlGyroscope(modifier = Modifier.fillMaxSize())
                                DockPanel.Resolution -> Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(8.dp)
                                ) {
                                    CustomResolutionContent(onValueCommitted = onRefreshWindowSize)
                                }
                                DockPanel.Music -> Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(12.dp)
                                ) {
                                    AfMusicSettings(Modifier.fillMaxWidth(), onOpenPlayer = { panel = null })
                                }
                                DockPanel.Game -> GameActionContent(
                                    modifier = Modifier.fillMaxSize(),
                                    onForceClose = onForceClose,
                                    onSwitchLog = onSwitchLog,
                                    enableTerracotta = enableTerracotta,
                                    onOpenTerracottaMenu = onOpenTerracottaMenu,
                                    onRefreshWindowSize = onRefreshWindowSize,
                                    onShowToast = onShowToast
                                )
                            }
                        }

            }
        }
    }
}

private class DockEntry(val icon: Int, val label: String, val onClick: () -> Unit, val active: Boolean)

@Composable
private fun AfGameDock(
    active: DockPanel?,
    onOpen: (DockPanel) -> Unit,
    onKeyboard: () -> Unit,
    onLog: () -> Unit,
    onForceClose: () -> Unit
) {
    val shape = AfCutShape(20.dp)
    val left = listOf(
        DockEntry(R.drawable.af_sym_control, "Controles", { onOpen(DockPanel.Controls) }, active == DockPanel.Controls),
        DockEntry(R.drawable.ic_touch_app_filled, "Gestos", { onOpen(DockPanel.Gestures) }, active == DockPanel.Gestures),
        DockEntry(R.drawable.ic_af_keyboard, "Teclado", onKeyboard, false),
        DockEntry(R.drawable.ic_mouse_filled, "Mouse", { onOpen(DockPanel.Mouse) }, active == DockPanel.Mouse),
        DockEntry(R.drawable.ic_mobile_rotate_filled, "Giroscópio", { onOpen(DockPanel.Gyro) }, active == DockPanel.Gyro)
    )
    val right = listOf(
        DockEntry(R.drawable.af_sym_renderer, "Resolução", { onOpen(DockPanel.Resolution) }, active == DockPanel.Resolution),
        DockEntry(R.drawable.af_sym_game, "Menu flutuante", { onOpen(DockPanel.Game) }, active == DockPanel.Game),
        DockEntry(R.drawable.af_sym_hud, "FPS e Memória", { onOpen(DockPanel.Game) }, false),
        DockEntry(R.drawable.ic_terminal_outlined, "Log", onLog, false),
        DockEntry(R.drawable.af_sym_music, "Música", { onOpen(DockPanel.Music) }, active == DockPanel.Music)
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.84f)
                .widthIn(max = 760.dp)
                .afHudFrame(backing = 0.62f)
                .afTechMotion()
                .afTechCorners(len = 14.dp, inset = 3.dp)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AfDockGrid(modifier = Modifier.weight(1f), entries = left)
            AfDockCenter(
                modifier = Modifier.width(210.dp),
                onGamepad = { onOpen(DockPanel.Gamepad) },
                onSettings = { onOpen(DockPanel.Game) }
            )
            AfDockGrid(modifier = Modifier.weight(1f), entries = right)
        }
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = "Toque num ícone para abrir. Toque de novo para fechar.",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.65f)
        )
        //Botão de forçar saída, colado embaixo do dock
        Row(
            modifier = Modifier
                .offset(y = (-6).dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFFB3123F), Color(0xFFE6366B))))
                .afClick(onForceClose)
                .padding(horizontal = 26.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("✕", color = Color.White, fontWeight = FontWeight.Bold)
            Text(
                "FORÇAR SAÍDA",
                color = Color.White,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun AfDockGrid(modifier: Modifier, entries: List<DockEntry>) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        entries.chunked(3).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth()) {
                row.forEach { entry ->
                    AfTechCell(
                        icon = entry.icon,
                        label = entry.label,
                        active = entry.active,
                        onClick = entry.onClick,
                        modifier = Modifier.weight(1f)
                    )
                }
                repeat(3 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun rememberClockText(): String {
    val fmt = remember { java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()) }
    var text by remember { mutableStateOf(fmt.format(java.util.Date())) }
    LaunchedEffect(Unit) {
        while (true) {
            text = fmt.format(java.util.Date())
            delay(15_000)
        }
    }
    return text
}

@Composable
private fun rememberBatteryPercent(): Int {
    val ctx = LocalContext.current
    var percent by remember { mutableIntStateOf(100) }
    LaunchedEffect(Unit) {
        while (true) {
            val i = ctx.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            if (i != null) {
                val level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level >= 0 && scale > 0) percent = level * 100 / scale
            }
            delay(30_000)
        }
    }
    return percent
}

@Composable
private fun AfDockCenter(modifier: Modifier, onGamepad: () -> Unit, onSettings: () -> Unit) {
    val shape = AfCutShape(16.dp)
    val clock = rememberClockText()
    val battery = rememberBatteryPercent()
    Column(
        modifier = modifier
            .afGlow(shape, AfAccent, 1.2f)
            .clip(shape)
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(clock, style = MaterialTheme.typography.titleSmall, color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(width = 24.dp, height = 12.dp)
                    .border(1.dp, Color.White, RoundedCornerShape(3.dp))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(battery / 100f)
                        .background(AfAccent)
                )
            }
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = "$battery%",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
        Text(
            modifier = Modifier.padding(vertical = 4.dp),
            text = "AfleckLauncher",
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = AfAccent,
            maxLines = 1
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(20.dp).afClick(onGamepad),
                painter = painterResource(R.drawable.af_sym_play),
                contentDescription = null,
                tint = Color.White
            )
            Text(
                modifier = Modifier.weight(1f),
                text = "AFLECK-MODE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp
            )
            Icon(
                modifier = Modifier.size(20.dp).afClick(onSettings),
                painter = painterResource(R.drawable.af_sym_settings),
                contentDescription = null,
                tint = AfAccent
            )
        }
    }
}

@Composable
private fun GameActionContent(
    onForceClose: () -> Unit,
    onSwitchLog: () -> Unit,
    enableTerracotta: Boolean,
    onOpenTerracottaMenu: () -> Unit,
    onRefreshWindowSize: () -> Unit,
    onShowToast: (AndroidStringText, Int) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = cardColor(false),
    contentColor: Color = onCardColor(),
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.lazyScrollWithBar(listState),
        state = listState,
        contentPadding = PaddingValues(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //强制关闭
        item {
            MenuTextButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_button_force_close),
                onClick = onForceClose,
                color = color,
                contentColor = contentColor,
            )
        }
        //日志输出
        item {
            MenuTextButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_switch_log),
                onClick = onSwitchLog,
                color = color,
                contentColor = contentColor,
            )
        }

        //如果开启多人联机，则展示这个按钮
        if (enableTerracotta) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            //打开联机菜单
            item {
                MenuTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.terracotta_menu),
                    onClick = onOpenTerracottaMenu,
                    color = color,
                    contentColor = contentColor,
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        //开启菜单悬浮窗
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_show_menu),
                switch = AllSettings.showMenuBall.state,
                onSwitch = { value ->
                    AllSettings.showMenuBall.save(value)
                    if (!value) {
                        onShowToast(
                            androidText(R.string.game_menu_option_show_menu_hided),
                            Toast.LENGTH_LONG
                        )
                    }
                },
                color = color,
                contentColor = contentColor,
            )
        }
        //菜单悬浮窗不透明度
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.game_menu_option_menu_ball_opacity),
                value = AllSettings.menuBallOpacity.state,
                valueRange = AllSettings.menuBallOpacity.floatRange,
                onValueChange = { value ->
                    AllSettings.menuBallOpacity.updateState(value)
                },
                onValueChangeFinished = { value ->
                    AllSettings.menuBallOpacity.save(value)
                },
                suffix = "%",
                color = color,
                contentColor = contentColor,
                enabled = AllSettings.showMenuBall.state
            )
        }
        //帧率显示
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_switch_fps),
                switch = AllSettings.showFPS.state,
                onSwitch = { AllSettings.showFPS.save(it) },
                color = color,
                contentColor = contentColor,
                enabled = AllSettings.showMenuBall.state
            )
        }
        //帧率显示模式
        item {
            MenuListLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.game_menu_option_fps_display_mode),
                items = FpsDisplayMode.entries,
                currentItem = AllSettings.fpsDisplayMode.state,
                onItemChange = { mode ->
                    AllSettings.fpsDisplayMode.save(mode)
                },
                getItemText = { stringResource(it.nameRes) },
                color = color,
                contentColor = contentColor,
                enabled = AllSettings.showMenuBall.state && AllSettings.showFPS.state
            )
        }
        //内存显示
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_switch_memory),
                switch = AllSettings.showMemory.state,
                onSwitch = { AllSettings.showMemory.save(it) },
                color = color,
                contentColor = contentColor,
                enabled = AllSettings.showMenuBall.state
            )
        }
        //CPU / GPU 使用率显示
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_switch_gpu_cpu),
                switch = AllSettings.showGpuCpu.state,
                onSwitch = { AllSettings.showGpuCpu.save(it) },
                color = color,
                contentColor = contentColor,
                enabled = AllSettings.showMenuBall.state
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 分辨率规则与游戏窗口分辨率
        item {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 分辨率规则
                MenuListLayout(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.settings_renderer_resolution_rule_title),
                    items = ResolutionRule.entries,
                    currentItem = AllSettings.resolutionRule.state,
                    onItemChange = { rule ->
                        AllSettings.resolutionRule.save(rule)
                        // 自定义分辨率尚未初始化时，以屏幕真实宽高填充
                        if (rule == ResolutionRule.CUSTOM) {
                            ensureCustomResolutionInitialized(context)
                        }
                        onRefreshWindowSize()
                    },
                    getItemText = { stringResource(it.nameRes) },
                    color = color,
                    contentColor = contentColor,
                )

                // 百分比分辨率
                AnimatedVisibility(
                    visible = AllSettings.resolutionRule.state == ResolutionRule.PERCENTAGE,
                    enter = fadeIn(animationSpec = getAnimateTween()) +
                            expandVertically(animationSpec = getAnimateTween()),
                    exit = fadeOut(animationSpec = getAnimateTween()) +
                            shrinkVertically(animationSpec = getAnimateTween())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        MenuSliderLayout(
                            modifier = Modifier.fillMaxWidth(),
                            title = stringResource(R.string.settings_renderer_resolution_scale_title),
                            value = AllSettings.resolutionRatio.state,
                            valueRange = AllSettings.resolutionRatio.floatRange,
                            onValueChange = { value ->
                                AllSettings.resolutionRatio.updateState(value)
                            },
                            onValueChangeFinished = { value ->
                                AllSettings.resolutionRatio.save(value)
                                onRefreshWindowSize()
                            },
                            suffix = "%",
                            color = color,
                            contentColor = contentColor,
                        )
                    }
                }

                // 自定义分辨率
                AnimatedVisibility(
                    visible = AllSettings.resolutionRule.state == ResolutionRule.CUSTOM,
                    enter = fadeIn(animationSpec = getAnimateTween()) +
                            expandVertically(animationSpec = getAnimateTween()),
                    exit = fadeOut(animationSpec = getAnimateTween()) +
                            shrinkVertically(animationSpec = getAnimateTween())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        CustomResolutionContent(onValueCommitted = onRefreshWindowSize)
                    }
                }
            }
        }
    }
}

/**
 * 自定义分辨率的宽高输入卡片
 */
@Composable
private fun CustomResolutionContent(
    onValueCommitted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val screenSize = remember(context) { getRealScreenSize(context) }

    BackgroundCard(
        modifier = modifier.fillMaxWidth(),
        influencedByBackground = false,
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_renderer_resolution_scale_title),
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IntInputField(
                    modifier = Modifier.weight(1f),
                    value = AllSettings.customResolutionWidth.state,
                    permitted = customResolutionRange(screenSize.width),
                    label = stringResource(R.string.settings_renderer_resolution_custom_width),
                    onValueChange = { value ->
                        AllSettings.customResolutionWidth.save(value)
                        onValueCommitted()
                    }
                )
                IntInputField(
                    modifier = Modifier.weight(1f),
                    value = AllSettings.customResolutionHeight.state,
                    permitted = customResolutionRange(screenSize.height),
                    label = stringResource(R.string.settings_renderer_resolution_custom_height),
                    onValueChange = { value ->
                        AllSettings.customResolutionHeight.save(value)
                        onValueCommitted()
                    }
                )
            }
        }
    }
}

@Composable
private fun ControlOverview(
    modifier: Modifier = Modifier,
    color: Color = cardColor(false),
    contentColor: Color = onCardColor(),
    closeScreen: () -> Unit,
    onInputMethod: () -> Unit,
    onSendKeycode: () -> Unit,
    onReplacementControl: () -> Unit,
    onEditLayout: () -> Unit
) {
    val listState = rememberLazyListState()
    val sdlEnabled by SdlBridge.enabled.collectAsState()

    LazyColumn(
        modifier = modifier.lazyScrollWithBar(listState),
        state = listState,
        contentPadding = PaddingValues(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //切换输入法
        item {
            MenuTextButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_input_method),
                onClick = {
                    onInputMethod()
                    closeScreen()
                },
                color = color,
                contentColor = contentColor,
            )
        }
        //自动唤起输入法（SDL）
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_auto_show_ime),
                switch = AllSettings.sdlAutoShowIme.state,
                onSwitch = { AllSettings.sdlAutoShowIme.save(it) },
                enabled = sdlEnabled,
                color = color,
                contentColor = contentColor,
            )
        }
        //输入时默认弹出放大镜
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_ime_magnifier_auto_show),
                switch = AllSettings.imeMagnifierAutoShow.state,
                onSwitch = { AllSettings.imeMagnifierAutoShow.save(it) },
                color = color,
                contentColor = contentColor,
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        //发送键值
        item {
            MenuTextButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_send_keycode),
                onClick = {
                    onSendKeycode()
                    closeScreen()
                },
                color = color,
                contentColor = contentColor,
            )
        }
        //更换控制布局
        item {
            MenuTextButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_replacement_control),
                onClick = {
                    onReplacementControl()
                    closeScreen()
                },
                color = color,
                contentColor = contentColor,
            )
        }
        //编辑布局
        item {
            MenuTextButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.control_manage_info_edit),
                onClick = {
                    onEditLayout()
                    closeScreen()
                },
                color = color,
                contentColor = contentColor,
            )
        }
        //控制布局不透明度
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.game_menu_option_controls_opacity),
                value = AllSettings.controlsOpacity.state,
                valueRange = AllSettings.controlsOpacity.floatRange,
                onValueChange = { AllSettings.controlsOpacity.updateState(it) },
                onValueChangeFinished = { AllSettings.controlsOpacity.save(it) },
                suffix = "%",
                color = color,
                contentColor = contentColor,
            )
        }
    }
}

@Composable
private fun ControlMouse(
    modifier: Modifier = Modifier,
    color: Color = cardColor(false),
    contentColor: Color = onCardColor(),
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.lazyScrollWithBar(listState),
        state = listState,
        contentPadding = PaddingValues(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //隐藏虚拟鼠标
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.settings_control_mouse_hide_title),
                switch = AllSettings.hideMouse.state,
                onSwitch = { AllSettings.hideMouse.save(it) },
                color = color,
                contentColor = contentColor,
                enabled = AllSettings.mouseControlMode.state == MouseControlMode.CLICK
            )
        }
        //触控板式操作
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.settings_control_mouse_enable_click_title),
                switch = AllSettings.enableMouseClick.state,
                onSwitch = { AllSettings.enableMouseClick.save(it) },
                color = color,
                contentColor = contentColor,
                enabled = AllSettings.mouseControlMode.state == MouseControlMode.SLIDE
            )
        }
        //鼠标控制模式
        item {
            MenuListLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_mouse_control_mode_title),
                items = MouseControlMode.entries,
                currentItem = AllSettings.mouseControlMode.state,
                onItemChange = { AllSettings.mouseControlMode.save(it) },
                getItemText = { stringResource(it.nameRes) },
                color = color,
                contentColor = contentColor,
            )
        }
        //阻止触摸虚拟鼠标输入
        item {
            MenuListLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_mouse_block_touch_title),
                items = MouseTouchBlockMode.entries,
                currentItem = AllSettings.mouseTouchBlockMode.state,
                onItemChange = { AllSettings.mouseTouchBlockMode.save(it) },
                getItemText = { stringResource(it.nameRes) },
                color = color,
                contentColor = contentColor,
            )
        }
        //虚拟鼠标大小
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_mouse_size_title),
                value = AllSettings.mouseSize.state,
                valueRange = AllSettings.mouseSize.floatRange,
                onValueChange = { AllSettings.mouseSize.updateState(it) },
                onValueChangeFinished = { AllSettings.mouseSize.save(it) },
                suffix = "Dp",
                color = color,
                contentColor = contentColor,
            )
        }
        //虚拟鼠标灵敏度
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_mouse_sensitivity_title),
                value = AllSettings.cursorSensitivity.state,
                valueRange = AllSettings.cursorSensitivity.floatRange,
                onValueChange = { AllSettings.cursorSensitivity.updateState(it) },
                onValueChangeFinished = { AllSettings.cursorSensitivity.save(it) },
                suffix = "%",
                color = color,
                contentColor = contentColor,
            )
        }
        //抓获鼠标滑动灵敏度
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_mouse_capture_sensitivity_title),
                value = AllSettings.mouseCaptureSensitivity.state,
                valueRange = AllSettings.mouseCaptureSensitivity.floatRange,
                onValueChange = { AllSettings.mouseCaptureSensitivity.updateState(it) },
                onValueChangeFinished = { AllSettings.mouseCaptureSensitivity.save(it) },
                suffix = "%",
                color = color,
                contentColor = contentColor,
            )
        }
        //虚拟鼠标长按触发的延迟
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_mouse_long_press_delay_title),
                value = AllSettings.mouseLongPressDelay.state,
                valueRange = AllSettings.mouseLongPressDelay.floatRange,
                onValueChange = { AllSettings.mouseLongPressDelay.updateState(it) },
                onValueChangeFinished = { AllSettings.mouseLongPressDelay.save(it) },
                suffix = "ms",
                color = color,
                contentColor = contentColor,
            )
        }
    }
}

@Composable
private fun ControlGamepad(
    gamepadViewModel: GamepadViewModel,
    modifier: Modifier = Modifier,
    color: Color = cardColor(false),
    contentColor: Color = onCardColor(),
) {
    val listState = rememberLazyListState()
    //重映射相关设置仅在映射模式下可用
    val remapEnabled = AllSettings.gamepadControl.state &&
        AllSettings.gamepadInputMode.state == GamepadInputMode.Mapped
    LazyColumn(
        modifier = modifier.lazyScrollWithBar(listState),
        state = listState,
        contentPadding = PaddingValues(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //手柄控制总开关
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.settings_gamepad_title),
                switch = AllSettings.gamepadControl.state,
                onSwitch = { AllSettings.gamepadControl.save(it) },
                color = color,
                contentColor = contentColor,
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        //手柄输入模式
        item {
            MenuListLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_gamepad_input_mode_title),
                items = GamepadInputMode.entries,
                currentItem = AllSettings.gamepadInputMode.state,
                onItemChange = { AllSettings.gamepadInputMode.save(it) },
                getItemText = { stringResource(it.titleRes) },
                color = color,
                contentColor = contentColor,
                enabled = AllSettings.gamepadControl.state,
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        //手柄死区缩放
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_gamepad_deadzone_title),
                value = AllSettings.gamepadDeadZoneScale.state,
                valueRange = AllSettings.gamepadDeadZoneScale.floatRange,
                enabled = remapEnabled,
                onValueChange = { AllSettings.gamepadDeadZoneScale.updateState(it) },
                onValueChangeFinished = { AllSettings.gamepadDeadZoneScale.save(it) },
                suffix = "%",
                color = color,
                contentColor = contentColor,
            )
        }

        //摇杆指针灵敏度
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_gamepad_cursor_sensitivity_title),
                value = AllSettings.gamepadCursorSensitivity.state,
                valueRange = AllSettings.gamepadCursorSensitivity.floatRange,
                enabled = remapEnabled,
                onValueChange = { AllSettings.gamepadCursorSensitivity.updateState(it) },
                onValueChangeFinished = { AllSettings.gamepadCursorSensitivity.save(it) },
                suffix = "%",
                color = color,
                contentColor = contentColor,
            )
        }

        //摇杆视角灵敏度
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_gamepad_camera_sensitivity_title),
                value = AllSettings.gamepadCameraSensitivity.state,
                valueRange = AllSettings.gamepadCameraSensitivity.floatRange,
                enabled = remapEnabled,
                onValueChange = { AllSettings.gamepadCameraSensitivity.updateState(it) },
                onValueChangeFinished = { AllSettings.gamepadCameraSensitivity.save(it) },
                suffix = "%",
                color = color,
                contentColor = contentColor,
            )
        }

        //手柄映射配置切换
        item {
            val list = remember(gamepadViewModel) {
                gamepadViewModel.getAllConfigKeys()
            }

            if (list.isNotEmpty()) {
                MenuListLayout(
                    modifier = Modifier.fillMaxWidth(),
                    title = stringResource(R.string.settings_gamepad_config_title),
                    items = list,
                    currentItem = AllSettings.gamepadMappingConfig.state,
                    onItemChange = { name ->
                        AllSettings.gamepadMappingConfig.save(name)
                        gamepadViewModel.reloadAllMappings()
                    },
                    getItemText = { it },
                    color = color,
                    contentColor = contentColor,
                    enabled = remapEnabled,
                )
            } else {
                MenuTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.settings_gamepad_config_no_items),
                    onClick = {},
                    color = color,
                    contentColor = contentColor,
                    enabled = false
                )
            }
        }
    }
}

@Composable
private fun ControlGesture(
    modifier: Modifier = Modifier,
    color: Color = cardColor(false),
    contentColor: Color = onCardColor(),
) {
    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.lazyScrollWithBar(listState),
        state = listState,
        contentPadding = PaddingValues(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //手势控制
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.settings_control_gesture_control_title),
                switch = AllSettings.gestureControl.state,
                onSwitch = { AllSettings.gestureControl.save(it) },
                color = color,
                contentColor = contentColor,
            )
        }

        //点击触发的操作类型
        item {
            MenuListLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_gesture_tap_action_title),
                items = GestureActionType.entries,
                currentItem = AllSettings.gestureTapMouseAction.state,
                onItemChange = { AllSettings.gestureTapMouseAction.save(it) },
                getItemText = { stringResource(it.nameRes) },
                color = color,
                contentColor = contentColor,
                enabled = AllSettings.gestureControl.state
            )
        }

        //长按触发的操作类型
        item {
            MenuListLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_gesture_long_press_action_title),
                items = GestureActionType.entries,
                currentItem = AllSettings.gestureLongPressMouseAction.state,
                onItemChange = { AllSettings.gestureLongPressMouseAction.save(it) },
                getItemText = { stringResource(it.nameRes) },
                color = color,
                contentColor = contentColor,
                enabled = AllSettings.gestureControl.state
            )
        }

        //手势长按触发的延迟
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_gesture_long_press_delay_title),
                value = AllSettings.gestureLongPressDelay.state,
                valueRange = AllSettings.gestureLongPressDelay.floatRange,
                enabled = AllSettings.gestureControl.state,
                onValueChange = { AllSettings.gestureLongPressDelay.updateState(it) },
                onValueChangeFinished = { AllSettings.gestureLongPressDelay.save(it) },
                suffix = "ms",
                color = color,
                contentColor = contentColor,
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        //快捷栏定位规则
        item {
            MenuListLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.game_menu_option_hotbar_rule),
                items = HotbarRule.entries,
                currentItem = AllSettings.hotbarRule.state,
                onItemChange = { AllSettings.hotbarRule.save(it) },
                getItemText = { stringResource(it.nameRes) },
                color = color,
                contentColor = contentColor,
            )
        }

        //快捷栏宽度
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.game_menu_option_hotbar_width),
                value = AllSettings.hotbarWidth.state / 10f,
                valueRange = 0f..100f,
                enabled = AllSettings.hotbarRule.state == HotbarRule.Custom,
                onValueChange = { value ->
                    AllSettings.hotbarWidth.updateState((value * 10f).toInt())
                },
                onValueChangeFinished = { value ->
                    AllSettings.hotbarWidth.save((value * 10f).toInt())
                },
                suffix = "%",
                color = color,
                contentColor = contentColor,
            )
        }

        //快捷栏高度
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.game_menu_option_hotbar_height),
                value = AllSettings.hotbarHeight.state / 10f,
                valueRange = 0f..100f,
                enabled = AllSettings.hotbarRule.state == HotbarRule.Custom,
                onValueChange = { value ->
                    AllSettings.hotbarHeight.updateState((value * 10f).toInt())
                },
                onValueChangeFinished = { value ->
                    AllSettings.hotbarHeight.save((value * 10f).toInt())
                },
                suffix = "%",
                color = color,
                contentColor = contentColor,
            )
        }

        //快捷栏双击与副手交换物品
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_hotbar_double_click),
                switch = AllSettings.hotbarDoubleClick.state,
                onSwitch = { AllSettings.hotbarDoubleClick.save(it) },
                color = color,
                contentColor = contentColor,
            )
        }

        //快捷栏长按丢弃所选物品
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.game_menu_option_hotbar_long_click),
                switch = AllSettings.hotbarLongClick.state,
                onSwitch = { AllSettings.hotbarLongClick.save(it) },
                color = color,
                contentColor = contentColor,
            )
        }

        //快捷栏长按快捷栏触发延迟
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.game_menu_option_hotbar_long_click_delay),
                value = AllSettings.hotbarLongClickDelay.state,
                valueRange = AllSettings.hotbarLongClickDelay.floatRange,
                onValueChange = { value ->
                    AllSettings.hotbarLongClickDelay.updateState(value)
                },
                onValueChangeFinished = { value ->
                    AllSettings.hotbarLongClickDelay.save(value)
                },
                suffix = "ms",
                enabled = AllSettings.hotbarLongClick.state,
                color = color,
                contentColor = contentColor,
            )
        }
    }
}

@Composable
private fun ControlGyroscope(
    modifier: Modifier = Modifier,
    color: Color = cardColor(false),
    contentColor: Color = onCardColor(),
) {
    val context = LocalContext.current
    val isGyroscopeAvailable = remember(context) {
        isGyroscopeAvailable(context = context)
    }

    val listState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.lazyScrollWithBar(listState),
        state = listState,
        contentPadding = PaddingValues(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //陀螺仪控制
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.settings_control_gyroscope_title),
                switch = AllSettings.gyroscopeControl.state,
                onSwitch = { AllSettings.gyroscopeControl.save(it) },
                color = color,
                contentColor = contentColor,
                enabled = isGyroscopeAvailable
            )
        }

        //陀螺仪控制灵敏度
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_gyroscope_sensitivity_title),
                value = AllSettings.gyroscopeSensitivity.state,
                valueRange = AllSettings.gyroscopeSensitivity.floatRange,
                enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state,
                onValueChange = { AllSettings.gyroscopeSensitivity.updateState(it) },
                onValueChangeFinished = { AllSettings.gyroscopeSensitivity.save(it) },
                suffix = "%",
                color = color,
                contentColor = contentColor,
            )
        }

        //陀螺仪采样率
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_gyroscope_sample_rate_title),
                value = AllSettings.gyroscopeSampleRate.state,
                valueRange = AllSettings.gyroscopeSampleRate.floatRange,
                enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state,
                onValueChange = { AllSettings.gyroscopeSampleRate.updateState(it) },
                onValueChangeFinished = { AllSettings.gyroscopeSampleRate.save(it) },
                suffix = "ms",
                color = color,
                contentColor = contentColor,
            )
        }

        //陀螺仪数值平滑
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.settings_control_gyroscope_smoothing_title),
                switch = AllSettings.gyroscopeSmoothing.state,
                onSwitch = { AllSettings.gyroscopeSmoothing.save(it) },
                color = color,
                contentColor = contentColor,
                enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state
            )
        }

        //陀螺仪平滑处理的窗口大小
        item {
            MenuSliderLayout(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(R.string.settings_control_gyroscope_smoothing_window_title),
                value = AllSettings.gyroscopeSmoothingWindow.state,
                valueRange = AllSettings.gyroscopeSmoothingWindow.floatRange,
                enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state && AllSettings.gyroscopeSmoothing.state,
                onValueChange = { AllSettings.gyroscopeSmoothingWindow.updateState(it) },
                onValueChangeFinished = { AllSettings.gyroscopeSmoothingWindow.save(it) },
                color = color,
                contentColor = contentColor,
            )
        }

        //反转 X 轴
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.settings_control_gyroscope_invert_x_title),
                switch = AllSettings.gyroscopeInvertX.state,
                onSwitch = { AllSettings.gyroscopeInvertX.save(it) },
                color = color,
                contentColor = contentColor,
                enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state
            )
        }

        //反转 Y 轴
        item {
            MenuSwitchButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.settings_control_gyroscope_invert_y_title),
                switch = AllSettings.gyroscopeInvertY.state,
                onSwitch = { AllSettings.gyroscopeInvertY.save(it) },
                color = color,
                contentColor = contentColor,
                enabled = isGyroscopeAvailable && AllSettings.gyroscopeControl.state
            )
        }
    }
}
/** Explicação curta (em português) do que cada painel faz. */
private fun dockHint(panel: DockPanel): String = when (panel) {
    DockPanel.Controls -> "Escolha, troque ou edite o layout dos botões na tela. Use \"Editar HUD\" para mover os botões do jeito que preferir."
    DockPanel.Gestures -> "Define o que cada toque faz: tocar, segurar, arrastar e usar dois dedos."
    DockPanel.Mouse -> "Ajusta o mouse virtual: tamanho, velocidade e como ele responde ao seu toque."
    DockPanel.Gamepad -> "Configura o controle (gamepad) conectado: botões e analógicos."
    DockPanel.Gyro -> "Mire mexendo o celular. Ligue o giroscópio e ajuste a sensibilidade."
    DockPanel.Resolution -> "Resolução menor dá mais FPS; maior deixa a imagem mais nítida. Aqui você também ajusta a barra de itens."
    DockPanel.Game -> "Ações do jogo: forçar saída, abrir o log, multiplayer e atualizar a tela."
    DockPanel.Music -> "Controle a música enquanto joga: tocar, pausar e trocar de faixa."
}
