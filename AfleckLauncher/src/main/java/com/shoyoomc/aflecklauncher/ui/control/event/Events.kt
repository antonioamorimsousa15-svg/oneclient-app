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

package com.shoyoomc.aflecklauncher.ui.control.event

import com.movtery.inputmap.keycodes.ControlEventKeycode
import org.lwjgl.glfw.CallbackBridge

/** Botão do mouse (esquerdo/direito) com suporte ao duplo clique do Afleck. */
fun afMouseButton(keycode: Int, pressed: Boolean) {
    if (AfDoubleClicker.isEligible(keycode)) AfDoubleClicker.handle(keycode, pressed)
    else {
        AfDoubleClicker.stop(keycode)
        CallbackBridge.sendMouseButton(keycode, pressed)
    }
}

/** Toque rápido: um clique normal ou, com o duplo clique ligado, dois cliques. */
fun afMouseTap(keycode: Int) {
    if (AfDoubleClicker.isEligible(keycode)) AfDoubleClicker.tap(keycode)
    else CallbackBridge.putMouseEvent(keycode)
}

/**
 * 点击按键时，处理LWJGL按键事件
 */
fun lwjglEvent(
    eventKey: String,
    isMouse: Boolean,
    isPressed: Boolean
) {
    val keycode: Int = ControlEventKeycode.getKeycodeFromEvent(eventKey)?.toInt() ?: return

    if (isMouse) {
        afMouseButton(keycode, isPressed)
    } else {
        CallbackBridge.sendKeyPress(keycode, CallbackBridge.getCurrentMods(), isPressed)
        CallbackBridge.setModifiers(keycode, isPressed)
    }
}

//启动器点击事件

/** 切换输入法 */
const val LAUNCHER_EVENT_SWITCH_IME = "launcher.event.switch_ime"
/** 切换菜单 */
const val LAUNCHER_EVENT_SWITCH_MENU = "launcher.event.switch_menu"
/** 控制虚拟鼠标滚轮上-长按一直触发 */
const val LAUNCHER_EVENT_SCROLL_UP = "launcher.event.scroll_up"
/** 控制虚拟鼠标滚轮上-单次点击 */
const val LAUNCHER_EVENT_SCROLL_UP_SINGLE = "launcher.event.scroll_up.single"
/** 控制虚拟鼠标滚轮下-长按一直触发 */
const val LAUNCHER_EVENT_SCROLL_DOWN = "launcher.event.scroll_down"
/** 控制虚拟鼠标滚轮下-单次点击 */
const val LAUNCHER_EVENT_SCROLL_DOWN_SINGLE = "launcher.event.scroll_down.single"

/**
 * 点击按键时，处理启动器事件
 */
fun launcherEvent(
    eventKey: String,
    isPressed: Boolean,
    onSwitchIME: () -> Unit,
    onSwitchMenu: () -> Unit,
    onSingleScrollUp: () -> Unit,
    onSingleScrollDown: () -> Unit,
    onLongScrollUp: () -> Unit,
    onLongScrollUpCancel: () -> Unit,
    onLongScrollDown: () -> Unit,
    onLongScrollDownCancel: () -> Unit
) {
    if (eventKey.startsWith("GLFW_MOUSE_", false)) {
        //处理鼠标事件
        lwjglEvent(eventKey = eventKey, isMouse = true, isPressed = isPressed)
    } else {
        if (isPressed) {
            when (eventKey) {
                LAUNCHER_EVENT_SWITCH_IME -> onSwitchIME()
                LAUNCHER_EVENT_SWITCH_MENU -> onSwitchMenu()
                LAUNCHER_EVENT_SCROLL_UP_SINGLE -> onSingleScrollUp()
                LAUNCHER_EVENT_SCROLL_DOWN_SINGLE -> onSingleScrollDown()
            }
        }
        when (eventKey) {
            LAUNCHER_EVENT_SCROLL_UP -> {
                if (isPressed) {
                    onLongScrollUp()
                } else {
                    onLongScrollUpCancel()
                }
            }
            LAUNCHER_EVENT_SCROLL_DOWN -> {
                if (isPressed) {
                    onLongScrollDown()
                } else {
                    onLongScrollDownCancel()
                }
            }
        }
    }
}

/**
 * Duplo clique: a cada toque no botão (bater / colocar bloco) o launcher dá dois cliques,
 * separados pelo intervalo escolhido (ms). O segundo clique fica apertado enquanto o dedo segurar.
 */
object AfDoubleClicker {
    fun isEligible(keycode: Int): Boolean =
        (keycode == com.movtery.inputmap.keycodes.LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT.toInt() ||
            keycode == com.movtery.inputmap.keycodes.LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_RIGHT.toInt()) &&
            AfMacro.enabled(keycode)

    /** Toque: clique, pausa do intervalo e segundo clique (ambos soltos em seguida). */
    fun tap(keycode: Int) {
        if (AfMacro.enabled(keycode)) { AfMacro.burst(keycode); return }
        stop(keycode)
        val gap = com.shoyoomc.aflecklauncher.ui.screens.main.AfThemeState.dblClickRate.coerceIn(20, 200).toLong()
        val short = (gap / 3).coerceAtLeast(10L)
        CallbackBridge.sendMouseButton(keycode, true)
        later(keycode, short) { CallbackBridge.sendMouseButton(keycode, false) }
        later(keycode, gap) { CallbackBridge.sendMouseButton(keycode, true) }
        later(keycode, gap + short) {
            CallbackBridge.sendMouseButton(keycode, false)
            pending[keycode]?.clear()
        }
    }

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val pending = HashMap<Int, MutableList<Runnable>>()

    private fun later(keycode: Int, delay: Long, block: () -> Unit) {
        val r = Runnable { block() }
        pending.getOrPut(keycode) { mutableListOf() }.add(r)
        handler.postDelayed(r, delay)
    }

    fun handle(keycode: Int, pressed: Boolean) {
        if (AfMacro.enabled(keycode)) {
            if (pressed) AfMacro.burst(keycode)
            else { AfMacro.cancel(keycode); CallbackBridge.sendMouseButton(keycode, false) }
            return
        }
        if (!pressed) {
            val wasWaiting = pending[keycode]?.isNotEmpty() == true
            stop(keycode)
            // se soltou antes do segundo clique, ele ainda acontece rápido e é solto logo depois
            CallbackBridge.sendMouseButton(keycode, false)
            if (wasWaiting) {
                CallbackBridge.sendMouseButton(keycode, true)
                CallbackBridge.sendMouseButton(keycode, false)
            }
            return
        }
        stop(keycode)
        val gap = com.shoyoomc.aflecklauncher.ui.screens.main.AfThemeState.dblClickRate.coerceIn(20, 200).toLong()
        CallbackBridge.sendMouseButton(keycode, true)
        later(keycode, (gap / 3).coerceAtLeast(10L)) { CallbackBridge.sendMouseButton(keycode, false) }
        later(keycode, gap) {
            pending[keycode]?.clear()
            CallbackBridge.sendMouseButton(keycode, true)
        }
    }

    fun stop(keycode: Int) {
        AfMacro.cancel(keycode)
        pending.remove(keycode)?.forEach { handler.removeCallbacks(it) }
    }
}
