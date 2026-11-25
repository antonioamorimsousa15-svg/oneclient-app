package com.shoyoomc.aflecklauncher.ui.control.event

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.movtery.inputmap.keycodes.LwjglGlfwKeycode
import org.lwjgl.glfw.CallbackBridge

/**
 * Macro embutido: um por botão do mouse (esquerdo e direito).
 * Cada toque no botão dispara uma rajada de [clicks] cliques (máximo 10) com intervalo fixo de [gap] ms.
 */
object AfMacro {
    const val MAX_CLICKS = 10
    private var prefs: SharedPreferences? = null

    var leftOn by mutableStateOf(false); private set
    var leftClicks by mutableIntStateOf(5); private set
    var leftGap by mutableIntStateOf(100); private set
    var rightOn by mutableStateOf(false); private set
    var rightClicks by mutableIntStateOf(5); private set
    var rightGap by mutableIntStateOf(100); private set

    fun load(ctx: Context) {
        val p = ctx.applicationContext.getSharedPreferences("afleck_ui", Context.MODE_PRIVATE)
        prefs = p
        leftOn = p.getBoolean("macro.leftOn", false)
        leftClicks = p.getInt("macro.leftClicks", 5)
        leftGap = p.getInt("macro.leftGap", 100)
        rightOn = p.getBoolean("macro.rightOn", false)
        rightClicks = p.getInt("macro.rightClicks", 5)
        rightGap = p.getInt("macro.rightGap", 100)
    }

    private fun save() {
        prefs?.edit()
            ?.putBoolean("macro.leftOn", leftOn)?.putInt("macro.leftClicks", leftClicks)?.putInt("macro.leftGap", leftGap)
            ?.putBoolean("macro.rightOn", rightOn)?.putInt("macro.rightClicks", rightClicks)?.putInt("macro.rightGap", rightGap)
            ?.apply()
    }

    fun changeLeftOn(v: Boolean) { leftOn = v; save() }
    fun changeLeftClicks(v: Int) { leftClicks = v.coerceIn(1, MAX_CLICKS); save() }
    fun changeLeftGap(v: Int) { leftGap = v.coerceIn(50, 500); save() }
    fun changeRightOn(v: Boolean) { rightOn = v; save() }
    fun changeRightClicks(v: Int) { rightClicks = v.coerceIn(1, MAX_CLICKS); save() }
    fun changeRightGap(v: Int) { rightGap = v.coerceIn(50, 500); save() }

    private val left get() = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_LEFT.toInt()
    private val right get() = LwjglGlfwKeycode.GLFW_MOUSE_BUTTON_RIGHT.toInt()

    fun enabled(keycode: Int): Boolean = (keycode == left && leftOn) || (keycode == right && rightOn)

    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private val pending = HashMap<Int, MutableList<Runnable>>()

    private fun post(keycode: Int, delay: Long, block: () -> Unit) {
        val r = Runnable { block() }
        pending.getOrPut(keycode) { mutableListOf() }.add(r)
        handler.postDelayed(r, delay)
    }

    /** Dispara a rajada do botão: [n] cliques, um a cada [gap] ms (intervalo fixo). */
    fun burst(keycode: Int) {
        cancel(keycode)
        val n = (if (keycode == left) leftClicks else rightClicks).coerceIn(1, MAX_CLICKS)
        val gap = (if (keycode == left) leftGap else rightGap).coerceIn(50, 500).toLong()
        val hold = (gap / 3).coerceAtLeast(10L)
        for (i in 0 until n) {
            post(keycode, i * gap) { CallbackBridge.sendMouseButton(keycode, true) }
            post(keycode, i * gap + hold) { CallbackBridge.sendMouseButton(keycode, false) }
        }
    }

    fun cancel(keycode: Int) {
        pending.remove(keycode)?.forEach { handler.removeCallbacks(it) }
    }
}
