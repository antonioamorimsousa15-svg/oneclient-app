package com.shoyoomc.aflecklauncher.ui.screens.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.math.roundToInt

/** Playlist do Spotify. */
class AfPlaylist(val id: String, val name: String, val uri: String)

/** Faixa de uma playlist. */
class AfTrack(val name: String, val artist: String, val uri: String)

/**
 * Player de música do Afleck.
 * Conecta na conta do Spotify (login PKCE, sem segredo) e controla a reprodução do app do Spotify
 * pela Web API oficial. Requer Client ID próprio (developer.spotify.com) e conta Premium.
 */
object AfMusic {
    private const val PREFS = "afleck_music"
    const val REDIRECT = "aflecklauncher://spotify"
    private const val SCOPES =
        "playlist-read-private playlist-read-collaborative user-read-playback-state user-modify-playback-state user-read-currently-playing"

    var showWidget by mutableStateOf(false)
    var pickerOpen by mutableStateOf(false)
    var offX by mutableStateOf(0f)
    var offY by mutableStateOf(0f)
    var clientId by mutableStateOf("")
    var loggedIn by mutableStateOf(false)
    var title by mutableStateOf("")
    var artist by mutableStateOf("")
    var playing by mutableStateOf(false)
    var status by mutableStateOf("")

    private var access = ""
    private var refresh = ""
    private var expiresAt = 0L

    private fun prefs(ctx: Context) = ctx.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(ctx: Context) {
        val p = prefs(ctx)
        showWidget = p.getBoolean("show", false)
        offX = p.getFloat("offX", 0f)
        offY = p.getFloat("offY", 0f)
        clientId = p.getString("clientId", "") ?: ""
        access = p.getString("access", "") ?: ""
        refresh = p.getString("refresh", "") ?: ""
        expiresAt = p.getLong("expires", 0L)
        loggedIn = refresh.isNotEmpty()
    }

    fun changeShow(ctx: Context, v: Boolean) { showWidget = v; prefs(ctx).edit().putBoolean("show", v).apply() }
    fun changeClientId(ctx: Context, v: String) { clientId = v.trim(); prefs(ctx).edit().putString("clientId", clientId).apply() }
    fun savePosition(ctx: Context) { prefs(ctx).edit().putFloat("offX", offX).putFloat("offY", offY).apply() }
    fun resetPosition(ctx: Context) { offX = 0f; offY = 0f; savePosition(ctx) }

    fun logout(ctx: Context) {
        access = ""; refresh = ""; expiresAt = 0L; loggedIn = false; title = ""; artist = ""; playing = false
        prefs(ctx).edit().remove("access").remove("refresh").remove("expires").apply()
    }

    private fun rand(n: Int): String {
        val b = ByteArray(n); SecureRandom().nextBytes(b)
        return Base64.encodeToString(b, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING).take(n)
    }

    private fun enc(s: String) = URLEncoder.encode(s, "UTF-8")

    /** Abre o navegador na tela de login do Spotify. */
    fun startLogin(ctx: Context) {
        if (clientId.isBlank()) { status = "Cole o Client ID do seu app Spotify primeiro."; return }
        val verifier = rand(64)
        val challenge = Base64.encodeToString(
            MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray()),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
        prefs(ctx).edit().putString("verifier", verifier).apply()
        val url = "https://accounts.spotify.com/authorize?client_id=${enc(clientId)}&response_type=code" +
            "&redirect_uri=${enc(REDIRECT)}&scope=${enc(SCOPES)}&code_challenge_method=S256&code_challenge=$challenge"
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (ctx !is Activity) i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(i)
        status = "Termine o login no navegador."
    }

    private fun http(method: String, url: String, body: String? = null, form: Boolean = false, auth: Boolean = true): Pair<Int, String> {
        return try {
            val c = URL(url).openConnection() as HttpURLConnection
            c.requestMethod = method
            c.connectTimeout = 12000; c.readTimeout = 12000
            if (auth) c.setRequestProperty("Authorization", "Bearer $access")
            if (body != null) {
                c.doOutput = true
                c.setRequestProperty("Content-Type", if (form) "application/x-www-form-urlencoded" else "application/json")
                c.outputStream.use { it.write(body.toByteArray()) }
            } else if (method == "PUT" || method == "POST") {
                c.doOutput = true
                c.setRequestProperty("Content-Length", "0")
                c.outputStream.use { }
            }
            val code = c.responseCode
            val stream = if (code in 200..299) c.inputStream else c.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() } ?: ""
            code to text
        } catch (e: Exception) {
            -1 to (e.message ?: "erro de rede")
        }
    }

    private fun applyToken(ctx: Context, json: JSONObject) {
        access = json.optString("access_token")
        json.optString("refresh_token").takeIf { it.isNotEmpty() }?.let { refresh = it }
        expiresAt = System.currentTimeMillis() + json.optLong("expires_in", 3600L) * 1000L
        prefs(ctx).edit().putString("access", access).putString("refresh", refresh).putLong("expires", expiresAt).commit()
        loggedIn = refresh.isNotEmpty()
    }

    /** Troca o código recebido do login por tokens. Roda em thread de fundo. */
    fun finishLogin(ctx: Context, code: String): Boolean {
        load(ctx)
        val verifier = prefs(ctx).getString("verifier", "") ?: ""
        val body = "grant_type=authorization_code&code=${enc(code)}&redirect_uri=${enc(REDIRECT)}" +
            "&client_id=${enc(clientId)}&code_verifier=${enc(verifier)}"
        val (c, t) = http("POST", "https://accounts.spotify.com/api/token", body, form = true, auth = false)
        return if (c == 200) { applyToken(ctx, JSONObject(t)); status = "Conectado ao Spotify."; true }
        else { status = "Falha no login ($c)."; false }
    }

    private fun ensureToken(ctx: Context): Boolean {
        if (refresh.isEmpty()) return false
        if (access.isNotEmpty() && System.currentTimeMillis() < expiresAt - 30000L) return true
        val body = "grant_type=refresh_token&refresh_token=${enc(refresh)}&client_id=${enc(clientId)}"
        val (c, t) = http("POST", "https://accounts.spotify.com/api/token", body, form = true, auth = false)
        if (c != 200) { status = "Sessão expirada. Entre de novo."; return false }
        applyToken(ctx, JSONObject(t))
        return true
    }

    private fun explain(code: Int): String = when (code) {
        204, 200 -> ""
        404 -> "Abra o app do Spotify e toque em qualquer música uma vez."
        403 -> "O Spotify recusou: precisa de conta Premium (e seu usuário adicionado no app)."
        401 -> "Sessão inválida. Entre de novo."
        429 -> "Muitas ações seguidas. Espere um pouco."
        -1 -> "Sem conexão."
        else -> "Erro do Spotify ($code)."
    }

    suspend fun playlists(ctx: Context): List<AfPlaylist> = withContext(Dispatchers.IO) {
        if (!ensureToken(ctx)) return@withContext emptyList()
        val (c, t) = http("GET", "https://api.spotify.com/v1/me/playlists?limit=50")
        if (c != 200) { status = explain(c); return@withContext emptyList() }
        val arr: JSONArray = JSONObject(t).optJSONArray("items") ?: JSONArray()
        (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            AfPlaylist(o.optString("id"), o.optString("name"), o.optString("uri"))
        }
    }

    suspend fun tracks(ctx: Context, playlistId: String): List<AfTrack> = withContext(Dispatchers.IO) {
        if (!ensureToken(ctx)) return@withContext emptyList()
        val (c, t) = http("GET", "https://api.spotify.com/v1/playlists/$playlistId/items?limit=100")
        if (c != 200) {
            status = if (c == 403 || c == 404) "Esta playlist não deixa listar as faixas. Use \"Tocar playlist\"." else explain(c)
            return@withContext emptyList()
        }
        val arr = JSONObject(t).optJSONArray("items") ?: JSONArray()
        (0 until arr.length()).mapNotNull { i ->
            val it = arr.optJSONObject(i) ?: return@mapNotNull null
            val tr = it.optJSONObject("item") ?: it.optJSONObject("track") ?: return@mapNotNull null
            val uri = tr.optString("uri")
            if (uri.isEmpty()) return@mapNotNull null
            val ar = tr.optJSONArray("artists")?.optJSONObject(0)?.optString("name") ?: ""
            AfTrack(tr.optString("name"), ar, uri)
        }
    }

    suspend fun play(ctx: Context, contextUri: String?, trackUri: String?) = withContext(Dispatchers.IO) {
        if (!ensureToken(ctx)) return@withContext
        val body = JSONObject().apply {
            if (contextUri != null) {
                put("context_uri", contextUri)
                if (trackUri != null) put("offset", JSONObject().put("uri", trackUri))
            } else if (trackUri != null) {
                put("uris", JSONArray().put(trackUri))
            }
        }.toString()
        val (c, _) = http("PUT", "https://api.spotify.com/v1/me/player/play", body)
        status = explain(c)
    }

    suspend fun toggle(ctx: Context) = withContext(Dispatchers.IO) {
        if (!ensureToken(ctx)) return@withContext
        val (c, _) = if (playing) http("PUT", "https://api.spotify.com/v1/me/player/pause")
        else http("PUT", "https://api.spotify.com/v1/me/player/play", "{}")
        status = explain(c)
        if (c in 200..299) playing = !playing
    }

    suspend fun skip(ctx: Context, next: Boolean) = withContext(Dispatchers.IO) {
        if (!ensureToken(ctx)) return@withContext
        val (c, _) = http("POST", "https://api.spotify.com/v1/me/player/" + if (next) "next" else "previous")
        status = explain(c)
    }

    suspend fun refreshNow(ctx: Context) = withContext(Dispatchers.IO) {
        if (!ensureToken(ctx)) return@withContext
        val (c, t) = http("GET", "https://api.spotify.com/v1/me/player/currently-playing")
        if (c == 200 && t.isNotEmpty()) {
            val o = JSONObject(t)
            val item = o.optJSONObject("item")
            title = item?.optString("name") ?: ""
            artist = item?.optJSONArray("artists")?.optJSONObject(0)?.optString("name") ?: ""
            playing = o.optBoolean("is_playing", false)
        }
    }
}

/** Opções do player: usadas em Aparência e na dock do jogo. */
@Composable
fun AfMusicSettings(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    LaunchedEffect(Unit) { AfMusic.load(ctx) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Player de música", color = AfText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("Mostra o player flutuante. Arraste para qualquer lugar da tela.", color = AfDim, fontSize = 12.sp)
            }
            Switch(
                checked = AfMusic.showWidget,
                onCheckedChange = { AfMusic.changeShow(ctx, it) },
                colors = SwitchDefaults.colors(checkedThumbColor = AfNavy, checkedTrackColor = AfAccent)
            )
        }
        Text("Client ID do app Spotify", color = AfDim, fontSize = 12.sp)
        BasicTextField(
            value = AfMusic.clientId,
            onValueChange = { AfMusic.changeClientId(ctx, it) },
            singleLine = true,
            textStyle = TextStyle(color = AfText, fontSize = 13.sp),
            cursorBrush = SolidColor(AfAccent),
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                .background(AfNavy.copy(alpha = 0.35f))
                .border(1.dp, AfBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                .padding(10.dp)
        )
        Text(
            "Crie um app grátis em developer.spotify.com e use este Redirect URI: ${AfMusic.REDIRECT}",
            color = AfDim, fontSize = 11.sp
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (AfMusic.loggedIn) {
                AfOutlineButton("Sair do Spotify", Modifier.weight(1f)) { AfMusic.logout(ctx) }
            } else {
                AfButton("Entrar no Spotify", Modifier.weight(1f)) { AfMusic.startLogin(ctx) }
            }
            AfButton("Escolher música", Modifier.weight(1f)) { AfMusic.load(ctx); AfMusic.pickerOpen = true }
        }
        AfOutlineButton("Voltar ao topo, no meio", Modifier.fillMaxWidth()) { AfMusic.resetPosition(ctx) }
        if (AfMusic.status.isNotEmpty()) Text(AfMusic.status, color = AfAccent, fontSize = 12.sp)
    }
}

/** Player flutuante e arrastável. Passe o alinhamento padrão (topo, no meio) no [modifier]. */
@Composable
fun AfMusicWidget(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { AfMusic.load(ctx) }
    LaunchedEffect(AfMusic.loggedIn, AfMusic.showWidget) {
        while (isActive && AfMusic.loggedIn && AfMusic.showWidget) {
            AfMusic.refreshNow(ctx)
            delay(6000)
        }
    }
    if (!AfMusic.showWidget) return
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .padding(top = 6.dp)
            .offset { IntOffset(AfMusic.offX.roundToInt(), AfMusic.offY.roundToInt()) }
            .widthIn(max = 340.dp)
            .clip(shape)
            .background(AfNavy.copy(alpha = 0.45f))
            .border(1.dp, AfBorder.copy(alpha = 0.8f), shape)
            .pointerInput(Unit) {
                detectDragGestures(onDragEnd = { AfMusic.savePosition(ctx) }) { change, drag ->
                    change.consume()
                    AfMusic.offX += drag.x
                    AfMusic.offY = (AfMusic.offY + drag.y).coerceAtLeast(0f)
                }
            }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(Modifier.widthIn(max = 150.dp)) {
            Text(
                if (AfMusic.title.isEmpty()) (if (AfMusic.loggedIn) "Nada tocando" else "Spotify desconectado") else AfMusic.title,
                color = AfText, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            if (AfMusic.artist.isNotEmpty()) Text(AfMusic.artist, color = AfDim, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text("«", color = AfAccent, fontSize = 20.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.afClick { scope.launch { AfMusic.skip(ctx, false) } })
        Text(if (AfMusic.playing) "II" else "▶", color = AfAccent, fontSize = 18.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.afClick { scope.launch { AfMusic.toggle(ctx) } })
        Text("»", color = AfAccent, fontSize = 20.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.afClick { scope.launch { AfMusic.skip(ctx, true) } })
        Text("≡", color = AfAccent, fontSize = 20.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.afClick { AfMusic.load(ctx); AfMusic.pickerOpen = true })
    }
}

/** Painel central para escolher playlist e música. */
@Composable
fun AfMusicPicker() {
    if (!AfMusic.pickerOpen) return
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val lists = remember { mutableStateListOf<AfPlaylist>() }
    val tracks = remember { mutableStateListOf<AfTrack>() }
    var selected by remember { mutableStateOf<AfPlaylist?>(null) }
    var loading by remember { mutableStateOf(false) }

    fun reload() {
        scope.launch {
            loading = true
            val r = AfMusic.playlists(ctx)
            lists.clear(); lists.addAll(r)
            loading = false
        }
    }
    LaunchedEffect(AfMusic.loggedIn) { if (AfMusic.loggedIn) reload() }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxWidth(0.7f).fillMaxHeight(0.8f)
                .afHudScreen(backing = 0.72f, glow = true).padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("MÚSICA // SPOTIFY", color = AfAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp, modifier = Modifier.weight(1f))
                Text("Atualizar", color = AfAccent, fontSize = 12.sp,
                    modifier = Modifier.afClick { AfMusic.load(ctx); if (AfMusic.loggedIn) reload() }.padding(horizontal = 10.dp))
                Text("✕", color = AfText, fontSize = 18.sp,
                    modifier = Modifier.afClick { AfMusic.pickerOpen = false }.padding(horizontal = 8.dp))
            }
            if (!AfMusic.loggedIn) {
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AfMusicSettings(Modifier.fillMaxWidth())
                }
            } else {
                if (AfMusic.status.isNotEmpty()) Text(AfMusic.status, color = AfDim, fontSize = 11.sp)
                Row(Modifier.fillMaxSize().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f).fillMaxHeight()) {
                        Text(if (loading) "Playlists (carregando...)" else "Playlists", color = AfDim, fontSize = 12.sp)
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(lists) { p ->
                                val sel = selected?.id == p.id
                                Text(
                                    p.name, color = if (sel) AfAccent else AfText, fontSize = 13.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                                        .background(AfNavy.copy(alpha = if (sel) 0.6f else 0.3f))
                                        .afClick {
                                            selected = p
                                            tracks.clear()
                                            scope.launch { val r = AfMusic.tracks(ctx, p.id); tracks.addAll(r) }
                                        }
                                        .padding(10.dp)
                                )
                            }
                        }
                    }
                    Column(Modifier.weight(1f).fillMaxHeight()) {
                        val pl = selected
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Músicas", color = AfDim, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            if (pl != null) Text("Tocar playlist", color = AfAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.afClick { scope.launch { AfMusic.play(ctx, pl.uri, null); AfMusic.refreshNow(ctx) } })
                        }
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(tracks) { t ->
                                Column(
                                    Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                                        .background(AfNavy.copy(alpha = 0.3f))
                                        .afClick {
                                            scope.launch {
                                                AfMusic.play(ctx, pl?.uri, t.uri)
                                                delay(800)
                                                AfMusic.refreshNow(ctx)
                                            }
                                        }
                                        .padding(10.dp)
                                ) {
                                    Text(t.name, color = AfText, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(t.artist, color = AfDim, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
