package com.shoyoomc.aflecklauncher.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.shoyoomc.aflecklauncher.ui.screens.main.AfMusic
import kotlin.concurrent.thread

/** Recebe o retorno do login do Spotify (aflecklauncher://spotify?code=...) e guarda a sessão. */
class SpotifyAuthActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val code = intent?.data?.getQueryParameter("code")
        if (code.isNullOrEmpty()) {
            Toast.makeText(this, "Login do Spotify cancelado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val app = applicationContext
        thread {
            val ok = try { AfMusic.finishLogin(app, code) } catch (e: Exception) { false }
            runOnUiThread {
                Toast.makeText(this, if (ok) "Spotify conectado!" else "Falha ao conectar no Spotify.", Toast.LENGTH_LONG).show()
                packageManager.getLaunchIntentForPackage(packageName)?.let {
                    it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(it)
                }
                finish()
            }
        }
    }
}
