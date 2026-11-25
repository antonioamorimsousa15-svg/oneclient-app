package com.shoyoomc.aflecklauncher.ui.screens.main

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import coil3.compose.AsyncImage
import com.shoyoomc.aflecklauncher.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.json.JSONObject
import org.json.JSONTokener
import kotlin.math.roundToInt

/**
 * Player de música do Afleck: abre o Spotify Web dentro do próprio launcher/jogo
 * (sem usar o app do Spotify). O cartão flutuante lê o que está tocando e controla a reprodução.
 */
object AfMusic {
    private const val PREFS = "afleck_music"
    private const val HOME_URL = "https://open.spotify.com/"
    private const val DESKTOP_UA =
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

    var showWidget by mutableStateOf(false)
    var panelOpen by mutableStateOf(false)
    var offX by mutableStateOf(0f)
    var offY by mutableStateOf(0f)
    var title by mutableStateOf("")
    var artist by mutableStateOf("")
    var artUrl by mutableStateOf("")
    var playing by mutableStateOf(false)
    var progress by mutableFloatStateOf(0f)

    private var web: WebView? = null
    private var suffixDone = false

    private fun prefs(ctx: Context) = ctx.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(ctx: Context) {
        val p = prefs(ctx)
        showWidget = p.getBoolean("show", false)
        offX = p.getFloat("offX", 0f)
        offY = p.getFloat("offY", 0f)
    }

    fun changeShow(ctx: Context, v: Boolean) {
        showWidget = v
        prefs(ctx).edit().putBoolean("show", v).apply()
        if (!v) stop()
    }

    fun savePosition(ctx: Context) { prefs(ctx).edit().putFloat("offX", offX).putFloat("offY", offY).apply() }
    fun resetPosition(ctx: Context) { offX = 0f; offY = 0f; savePosition(ctx) }

    /** Abre o painel central com o Spotify (liga o player se estiver desligado). */
    fun openPanel(ctx: Context) {
        if (!showWidget) changeShow(ctx, true)
        panelOpen = true
    }

    /** Desliga o player e para a música. */
    private fun stop() {
        panelOpen = false
        web?.let { w ->
            (w.parent as? ViewGroup)?.removeView(w)
            w.destroy()
        }
        web = null
        title = ""; artist = ""; artUrl = ""; playing = false; progress = 0f
    }

    /** O jogo roda em outro processo: cada processo precisa de uma pasta de dados própria para o WebView. */
    private fun initSuffix(ctx: Context) {
        if (suffixDone) return
        suffixDone = true
        if (Build.VERSION.SDK_INT >= 28) {
            val p = Application.getProcessName()
            if (p != ctx.packageName) {
                try { WebView.setDataDirectorySuffix(p.substringAfter(':', "game")) } catch (_: Exception) {}
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun webView(ctx: Context): WebView {
        web?.let { w ->
            (w.parent as? ViewGroup)?.removeView(w)
            return w
        }
        val app = ctx.applicationContext
        initSuffix(app)
        val w = WebView(app)
        w.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            userAgentString = DESKTOP_UA
            useWideViewPort = true
            loadWithOverviewMode = true
        }
        CookieManager.getInstance().apply {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(w, true)
        }
        w.webChromeClient = object : WebChromeClient() {
            // libera o DRM (Widevine) que o player do Spotify precisa para tocar
            override fun onPermissionRequest(request: PermissionRequest) {
                request.grant(request.resources)
            }
        }
        w.webViewClient = WebViewClient()
        w.setBackgroundColor(0xFF121212.toInt())
        w.loadUrl(HOME_URL)
        web = w
        return w
    }

    fun reload() { web?.reload() }

    private val READ_JS = """
        (function(){try{
          var m=navigator.mediaSession,md=m&&m.metadata,a='';
          if(md&&md.artwork&&md.artwork.length){a=md.artwork[md.artwork.length-1].src}
          var pr=document.querySelector('[data-testid="playback-progressbar"] input'),p=0;
          if(pr&&pr.max>0){p=pr.value/pr.max}
          return JSON.stringify({t:md?md.title:'',a:md?md.artist:'',i:a,s:m?m.playbackState:'',p:p});
        }catch(e){return '{}'}})()
    """.trimIndent()

    /** Lê título, artista, capa e estado do player. */
    fun poll() {
        val w = web ?: return
        w.evaluateJavascript(READ_JS) { raw ->
            try {
                val s = JSONTokener(raw).nextValue() as? String ?: return@evaluateJavascript
                val o = JSONObject(s)
                title = o.optString("t")
                artist = o.optString("a")
                artUrl = o.optString("i")
                playing = o.optString("s") == "playing"
                progress = o.optDouble("p", 0.0).toFloat().coerceIn(0f, 1f)
            } catch (_: Exception) {}
        }
    }

    private fun click(testId: String) {
        web?.evaluateJavascript(
            "var b=document.querySelector('[data-testid=\"" + testId + "\"]');if(b)b.click();", null
        )
    }

    fun toggle() { click("control-button-playpause"); playing = !playing }
    fun next() = click("control-button-skip-forward")
    fun previous() = click("control-button-skip-back")
}

/** Opções do player: usadas em Aparência e na dock do jogo. */
@Composable
fun AfMusicSettings(modifier: Modifier = Modifier, onOpenPlayer: () -> Unit = {}) {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { AfMusic.load(ctx) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_af_music), null, tint = AfAccent, modifier = Modifier.size(28.dp))
            Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                Text("Player de música", color = AfText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text(
                    "Spotify dentro do launcher e do jogo, em um cartão flutuante que você arrasta para onde quiser.",
                    color = AfDim, fontSize = 12.sp
                )
            }
            Switch(
                checked = AfMusic.showWidget,
                onCheckedChange = { AfMusic.changeShow(ctx, it) },
                colors = SwitchDefaults.colors(checkedThumbColor = AfNavy, checkedTrackColor = AfAccent)
            )
        }
        AfButton("Abrir Spotify", Modifier.fillMaxWidth()) {
            AfMusic.openPanel(ctx)
            onOpenPlayer()
        }
        AfOutlineButton("Voltar ao topo, no meio", Modifier.fillMaxWidth()) { AfMusic.resetPosition(ctx) }
        Text(
            "Entre na sua conta Spotify dentro do painel. Se ele pedir, use e-mail e senha.",
            color = AfDim, fontSize = 11.sp
        )
    }
}

/** Compatibilidade: o painel agora faz parte de [AfMusicWidget]. */
@Composable
fun AfMusicPicker() {}

/**
 * Cartão flutuante (arrastável) + painel central com o Spotify.
 * Deve ficar sempre composto enquanto o player estiver ligado, senão a música para.
 */
@Composable
fun AfMusicWidget(
    modifier: Modifier = Modifier,
    showCapsule: Boolean = true,
    pauseOnStop: Boolean = false
) {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { AfMusic.load(ctx) }
    if (!AfMusic.showWidget) return

    val webContent = remember {
        movableContentOf {
            AndroidView(factory = { AfMusic.webView(it) }, modifier = Modifier.fillMaxSize())
        }
    }

    LaunchedEffect(Unit) {
        while (isActive) {
            AfMusic.poll()
            delay(2000)
        }
    }

    if (pauseOnStop) {
        val owner = remember(ctx) {
            var c: Context = ctx
            while (c is ContextWrapper && c !is LifecycleOwner) c = c.baseContext
            c as? LifecycleOwner
        }
        DisposableEffect(owner) {
            val obs = LifecycleEventObserver { _, e ->
                if (e == Lifecycle.Event.ON_STOP && AfMusic.playing) AfMusic.toggle()
            }
            owner?.lifecycle?.addObserver(obs)
            onDispose { owner?.lifecycle?.removeObserver(obs) }
        }
    }

    Box(modifier.fillMaxSize()) {
        if (AfMusic.panelOpen) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    Modifier.fillMaxWidth(0.74f).fillMaxHeight(0.82f)
                        .afHudScreen(backing = 0.72f, glow = true).padding(6.dp)
                ) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painterResource(R.drawable.ic_af_music), null, tint = AfText, modifier = Modifier.size(20.dp))
                        Text(
                            "  MÚSICA // SPOTIFY", color = AfText, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp, modifier = Modifier.weight(1f)
                        )
                        Text("Recarregar", color = AfText, fontSize = 12.sp,
                            modifier = Modifier.afClick { AfMusic.reload() }.padding(horizontal = 10.dp))
                        Text("✕", color = AfText, fontSize = 18.sp,
                            modifier = Modifier.afClick { AfMusic.panelOpen = false }.padding(horizontal = 8.dp))
                    }
                    Box(Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(12.dp))) { webContent() }
                }
            }
        } else {
            // fora do painel o Spotify continua vivo (e tocando) num quadradinho de 1 dp
            Box(Modifier.size(1.dp).align(Alignment.BottomEnd)) { webContent() }
        }

        if (showCapsule) {
            val shape = RoundedCornerShape(30.dp)
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 6.dp)
                    .offset { IntOffset(AfMusic.offX.roundToInt(), AfMusic.offY.roundToInt()) }
                    .widthIn(max = 380.dp)
                    .clip(shape)
                    .background(AfNavy.copy(alpha = 0.55f))
                    .border(1.dp, AfBorder.copy(alpha = 0.8f), shape)
                    .pointerInput(Unit) {
                        detectDragGestures(onDragEnd = { AfMusic.savePosition(ctx) }) { change, drag ->
                            change.consume()
                            AfMusic.offX += drag.x
                            AfMusic.offY = (AfMusic.offY + drag.y).coerceAtLeast(0f)
                        }
                    }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    Modifier.size(46.dp).clip(RoundedCornerShape(16.dp))
                        .background(AfAccent.copy(alpha = 0.2f))
                        .afClick { AfMusic.panelOpen = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (AfMusic.artUrl.isNotEmpty()) {
                        AsyncImage(
                            model = AfMusic.artUrl, contentDescription = null,
                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(painterResource(R.drawable.ic_af_music), null, tint = AfAccent, modifier = Modifier.size(24.dp))
                    }
                }
                Column(
                    Modifier.widthIn(min = 90.dp, max = 140.dp).afClick { AfMusic.panelOpen = true },
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        if (AfMusic.title.isEmpty()) "Spotify" else AfMusic.title,
                        color = AfText, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                        maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        if (AfMusic.artist.isEmpty()) "Toque para abrir" else AfMusic.artist,
                        color = AfDim, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                    Box(Modifier.fillMaxWidth().padding(top = 2.dp).size(width = 140.dp, height = 3.dp).clip(CircleShape).background(AfDim.copy(alpha = 0.3f))) {
                        Box(Modifier.fillMaxHeight().fillMaxWidth(AfMusic.progress).background(AfAccent))
                    }
                }
                Icon(
                    painterResource(R.drawable.ic_af_skip_prev), null, tint = AfText,
                    modifier = Modifier.size(28.dp).afClick { AfMusic.previous() }
                )
                Box(
                    Modifier.size(42.dp).clip(CircleShape).background(AfAccent).afClick { AfMusic.toggle() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(if (AfMusic.playing) R.drawable.ic_af_pause else R.drawable.ic_play_arrow_filled),
                        null, tint = AfNavy, modifier = Modifier.size(24.dp)
                    )
                }
                Icon(
                    painterResource(R.drawable.ic_af_skip_next), null, tint = AfText,
                    modifier = Modifier.size(28.dp).afClick { AfMusic.next() }
                )
            }
        }
    }
}
