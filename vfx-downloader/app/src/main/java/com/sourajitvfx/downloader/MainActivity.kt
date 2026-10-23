package com.sourajitvfx.downloader

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.ffmpeg.FFmpeg
import java.io.File
import java.util.UUID
import java.util.concurrent.Executors
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private val bg = Color.rgb(8, 11, 18)
    private val panel = Color.rgb(19, 24, 37)
    private val purple = Color.rgb(157, 92, 255)
    private val cyan = Color.rgb(55, 226, 255)
    private val white = Color.rgb(244, 246, 255)
    private val muted = Color.rgb(153, 163, 184)
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var urlInput: EditText
    private lateinit var status: TextView
    private lateinit var downloadButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var qualityGroup: RadioGroup
    private var audioOnly = false
    private var busy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        buildUi()
        handleIncomingIntent(intent)
        executor.execute {
            try {
                YoutubeDL.getInstance().init(applicationContext)
                FFmpeg.getInstance().init(applicationContext)
                runOnUiThread { status.text = "READY • Download only content you have permission to save" }
            } catch (e: Exception) {
                runOnUiThread { status.text = "ENGINE INIT ISSUE • ${e.message ?: "Check runtime setup"}" }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun buildUi() {
        val scroll = ScrollView(this).apply { setBackgroundColor(bg); isFillViewport = true }
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(30))
            background = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(bg, Color.rgb(15, 10, 33), bg))
        }
        scroll.addView(root)
        setContentView(scroll)

        val brandRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val logo = TextView(this).apply {
            text = "VFX"; textSize = 19f; typeface = Typeface.DEFAULT_BOLD; gravity = Gravity.CENTER
            setTextColor(Color.WHITE); background = rounded(purple, 15); setPadding(dp(12), dp(12), dp(12), dp(12))
        }
        brandRow.addView(logo, LinearLayout.LayoutParams(dp(58), dp(54)))
        val brand = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), 0, 0, 0) }
        brand.addView(label("VFX DOWNLOADER", 19f, white, true))
        brand.addView(label("POWERED BY SOURAJIT VFX", 10f, cyan, true))
        brandRow.addView(brand, LinearLayout.LayoutParams(0, -2, 1f))
        brandRow.addView(TextView(this).apply {
            text = "BETA"; textSize = 10f; setTextColor(cyan); background = rounded(Color.rgb(18, 47, 62), 10)
            setPadding(dp(10), dp(6), dp(10), dp(6))
        })
        root.addView(brandRow)
        root.addView(space(23))
        root.addView(label("Your links. Your library.", 27f, white, true))
        root.addView(label("A neon-styled media saver for public, permitted media.", 13f, muted, false).apply { setPadding(0, dp(7), 0, 0) })
        root.addView(space(22))

        val entryCard = card()
        entryCard.addView(label("PASTE MEDIA URL", 11f, cyan, true))
        urlInput = EditText(this).apply {
            hint = "Paste YouTube / Instagram / other link…"
            setHintTextColor(Color.rgb(107, 119, 142)); setTextColor(white); textSize = 14f
            setSingleLine(true); inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_URI
            background = rounded(Color.rgb(9, 13, 23), 13); setPadding(dp(13), dp(13), dp(13), dp(13))
        }
        val urlRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        urlRow.addView(urlInput, LinearLayout.LayoutParams(0, dp(52), 1f))
        urlRow.addView(makeButton("PASTE", panel, cyan) { pasteUrl() }, LinearLayout.LayoutParams(dp(68), dp(52)).apply { marginStart = dp(8) })
        entryCard.addView(space(12)); entryCard.addView(urlRow)
        entryCard.addView(space(14))
        entryCard.addView(label("DOWNLOAD TYPE", 10f, muted, true))
        val formatGroup = RadioGroup(this).apply { orientation = RadioGroup.HORIZONTAL }
        val videoRadio = makeRadio("Video (MP4)", true)
        val audioRadio = makeRadio("Audio (MP3)", false)
        formatGroup.addView(videoRadio); formatGroup.addView(audioRadio)
        formatGroup.setOnCheckedChangeListener { _, checked -> audioOnly = checked == audioRadio.id }
        entryCard.addView(formatGroup)
        entryCard.addView(label("QUALITY", 10f, muted, true).apply { setPadding(0, dp(10), 0, 0) })
        qualityGroup = RadioGroup(this).apply { orientation = RadioGroup.HORIZONTAL }
        qualityGroup.addView(makeRadio("Best", true, 301))
        qualityGroup.addView(makeRadio("720p", false, 302))
        qualityGroup.addView(makeRadio("480p", false, 303))
        entryCard.addView(qualityGroup)
        root.addView(entryCard)
        root.addView(space(14))

        downloadButton = makeButton("⬇   START DOWNLOAD", purple, Color.WHITE) { startDownload() }
        root.addView(downloadButton, LinearLayout.LayoutParams(-1, dp(54)))
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100; progress = 0; visibility = View.GONE
        }
        root.addView(space(8)); root.addView(progressBar, LinearLayout.LayoutParams(-1, dp(5)))
        status = label("INITIALIZING DOWNLOAD ENGINE…", 11f, muted, false).apply { setPadding(0, dp(8), 0, 0) }
        root.addView(status)
        root.addView(space(22))

        val featureRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        featureRow.addView(featureTile("🌐", "MULTI-SITE", "yt-dlp engine"), LinearLayout.LayoutParams(0, dp(104), 1f))
        featureRow.addView(spaceWidth(8))
        featureRow.addView(featureTile("🎧", "AUDIO", "FFmpeg support"), LinearLayout.LayoutParams(0, dp(104), 1f))
        featureRow.addView(spaceWidth(8))
        featureRow.addView(featureTile("📁", "LOCAL", "Save to Downloads"), LinearLayout.LayoutParams(0, dp(104), 1f))
        root.addView(featureRow)
        root.addView(space(20))
        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        actions.addView(makeButton("OPEN DOWNLOADS", panel, white) { openDownloads() }, LinearLayout.LayoutParams(0, dp(46), 1f))
        actions.addView(spaceWidth(10))
        actions.addView(makeButton("SUPPORT / UPI", panel, cyan) { showSupport() }, LinearLayout.LayoutParams(0, dp(46), 1f))
        root.addView(actions)
        root.addView(space(24))
        root.addView(label("USE RESPONSIBLY", 11f, cyan, true))
        root.addView(label("Download only content you own, created, or are authorized to save. Private, DRM-protected, login-gated or restricted media may not work. Platform rules and copyright law still apply.", 12f, muted, false).apply { setPadding(0, dp(7), 0, 0) })
        root.addView(space(14))
        root.addView(label("VFX DOWNLOADER • v1.0.0", 10f, Color.rgb(90, 101, 122), true).apply { gravity = Gravity.CENTER })
    }

    private fun startDownload() {
        val link = urlInput.text?.toString()?.trim().orEmpty()
        if (link.isBlank()) { urlInput.error = "Paste a media URL first"; return }
        val uri = runCatching { Uri.parse(link) }.getOrNull()
        if (uri == null || uri.scheme !in listOf("http", "https") || uri.host.isNullOrBlank()) {
            urlInput.error = "Enter a valid http/https URL"; return
        }
        if (busy) return
        busy = true
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(urlInput.windowToken, 0)
        downloadButton.isEnabled = false
        progressBar.visibility = View.VISIBLE
        progressBar.progress = 0
        status.text = "READING MEDIA INFO…"
        val qualityId = qualityGroup.checkedRadioButtonId
        executor.execute {
            try {
                val info = YoutubeDL.getInstance().getInfo(link)
                val title = info.title ?: "Media"
                val outDir = File(getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS), "VFX-Downloader")
                if (!outDir.exists() && !outDir.mkdirs()) throw IllegalStateException("Cannot create download folder")
                val request = YoutubeDLRequest(link)
                request.addOption("--no-playlist")
                request.addOption("--no-mtime")
                request.addOption("--newline")
                request.addOption("--no-warnings")
                request.addOption("-o", File(outDir, "%(title).180B.%(ext)s").absolutePath)
                if (audioOnly) {
                    request.addOption("-f", "bestaudio/best")
                    request.addOption("-x")
                    request.addOption("--audio-format", "mp3")
                } else {
                    when (qualityId) {
                        302 -> request.addOption("-f", "best[height<=720][ext=mp4]/best[height<=720]/best")
                        303 -> request.addOption("-f", "best[height<=480][ext=mp4]/best[height<=480]/best")
                        else -> request.addOption("-f", "best[ext=mp4]/best")
                    }
                    request.addOption("--merge-output-format", "mp4")
                }
                runOnUiThread { status.text = "DOWNLOADING • ${title.take(50)}" }
                YoutubeDL.getInstance().execute(request, UUID.randomUUID().toString()) { progress, eta, line ->
                    runOnUiThread {
                        progressBar.progress = progress.roundToInt().coerceIn(0, 100)
                        status.text = "DOWNLOADING ${progress.roundToInt().coerceIn(0, 100)}% • ETA ${eta.coerceAtLeast(0)}s"
                    }
                }
                runOnUiThread {
                    progressBar.progress = 100
                    status.text = "DOWNLOAD COMPLETE • Saved in app's VFX-Downloader folder"
                    Toast.makeText(this, "Download complete", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                runOnUiThread { status.text = "DOWNLOAD FAILED • ${e.message?.take(140) ?: "Unsupported URL or network issue"}" }
            } finally {
                runOnUiThread { busy = false; downloadButton.isEnabled = true }
            }
        }
    }

    private fun showSupport() {
        val input = EditText(this).apply {
            hint = "yourname@upi"; setTextColor(white); setHintTextColor(muted)
            setSingleLine(true); background = rounded(Color.rgb(9, 13, 23), 10); setPadding(dp(12), dp(11), dp(12), dp(11))
        }
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(18), dp(8), dp(18), 0) }
        box.addView(label("Support VFX Downloader", 18f, white, true))
        box.addView(label("Enter your own valid UPI ID. This opens a UPI payment request; it does not verify that money was received. This is not a FamPay merchant-gateway integration.", 12f, muted, false).apply { setPadding(0, dp(8), 0, dp(12)) })
        box.addView(input)
        android.app.AlertDialog.Builder(this).setView(box)
            .setNegativeButton("CANCEL", null)
            .setNeutralButton("SHOW UPI DETAILS") { _, _ ->
                val vpa = input.text.toString().trim()
                if (!isVpa(vpa)) Toast.makeText(this, "Enter a valid UPI ID first", Toast.LENGTH_LONG).show()
                else showUpiDetails(vpa)
            }
            .setPositiveButton("OPEN UPI APP") { _, _ ->
                val vpa = input.text.toString().trim()
                if (!isVpa(vpa)) {
                    Toast.makeText(this, "Enter a valid UPI ID first", Toast.LENGTH_LONG).show()
                } else {
                    val upi = Uri.parse("upi://pay").buildUpon().appendQueryParameter("pa", vpa)
                        .appendQueryParameter("pn", "VFX Downloader Support")
                        .appendQueryParameter("tn", "Support VFX Downloader")
                        .appendQueryParameter("cu", "INR").build()
                    runCatching { startActivity(Intent(Intent.ACTION_VIEW, upi)) }
                        .onFailure { Toast.makeText(this, "No UPI app found on this device", Toast.LENGTH_LONG).show() }
                }
            }.show()
    }

    private fun isVpa(value: String): Boolean = value.matches(Regex("[A-Za-z0-9.\\-_]{2,256}@[A-Za-z0-9]{2,64}"))

    private fun showUpiDetails(vpa: String) {
        val upi = Uri.parse("upi://pay").buildUpon().appendQueryParameter("pa", vpa)
            .appendQueryParameter("pn", "VFX Downloader Support")
            .appendQueryParameter("cu", "INR").appendQueryParameter("tn", "Support VFX Downloader").build().toString()
        val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(dp(20), dp(12), dp(20), dp(8)) }
        box.addView(label("UPI PAYMENT DETAILS", 14f, cyan, true))
        box.addView(label(vpa, 18f, white, true).apply { setPadding(0, dp(10), 0, dp(10)) })
        box.addView(label("This is a payment link, not a scannable QR. Configure a QR generated by your payment provider for this exact UPI ID before advertising QR payments.", 12f, muted, false))
        android.app.AlertDialog.Builder(this).setTitle("Support VFX Downloader").setView(box)
            .setNegativeButton("CLOSE", null).setPositiveButton("COPY PAYMENT LINK") { _, _ ->
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("UPI payment URI", upi))
                Toast.makeText(this, "UPI payment link copied", Toast.LENGTH_LONG).show()
            }.show()
    }

    private fun openDownloads() {
        val dir = File(getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS), "VFX-Downloader")
        val files = dir.listFiles()?.sortedByDescending { it.lastModified() }.orEmpty()
        if (files.isEmpty()) {
            android.app.AlertDialog.Builder(this).setTitle("Downloads").setMessage("No downloaded files yet. Completed media is stored in this app's VFX-Downloader folder.")
                .setPositiveButton("OK", null).show(); return
        }
        val names = files.map { "${it.name}\n${android.text.format.Formatter.formatFileSize(this, it.length())}" }.toTypedArray()
        android.app.AlertDialog.Builder(this).setTitle("VFX Downloads (${files.size})").setItems(names) { _, which ->
            val file = files[which]
            val mime = when (file.extension.lowercase()) { "mp4", "mkv", "webm" -> "video/*"; "mp3", "m4a", "opus" -> "audio/*"; else -> "*/*" }
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            runCatching { startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(uri, mime).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)) }
                .onFailure { Toast.makeText(this, "No app can open this file", Toast.LENGTH_SHORT).show() }
        }.show()
    }

    private fun pasteUrl() {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val text = cm.primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(this)?.toString().orEmpty()
        if (text.isBlank()) Toast.makeText(this, "Clipboard has no text", Toast.LENGTH_SHORT).show()
        else { urlInput.setText(text.trim()); urlInput.setSelection(urlInput.text.length) }
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val shared = intent.getStringExtra(Intent.EXTRA_TEXT).orEmpty()
            if (shared.isNotBlank() && ::urlInput.isInitialized) urlInput.setText(shared.trim())
        } else if (intent?.action == Intent.ACTION_VIEW) {
            intent.dataString?.let { if (::urlInput.isInitialized) urlInput.setText(it) }
        }
    }

    private fun makeRadio(text: String, checked: Boolean, id: Int = View.generateViewId()): RadioButton = RadioButton(this).apply {
        this.id = id; this.text = text; isChecked = checked; textSize = 12f; setTextColor(white)
        buttonTintList = android.content.res.ColorStateList.valueOf(purple)
        setPadding(0, 0, dp(8), 0)
    }

    private fun card(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL; setPadding(dp(15), dp(15), dp(15), dp(15)); background = rounded(panel, 20)
    }

    private fun featureTile(emoji: String, title: String, sub: String) = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(dp(4), dp(12), dp(4), dp(12)); background = rounded(panel, 16)
        addView(label(emoji, 20f, cyan, true).apply { gravity = Gravity.CENTER })
        addView(label(title, 10f, white, true).apply { gravity = Gravity.CENTER; setPadding(0, dp(6), 0, 0) })
        addView(label(sub, 9f, muted, false).apply { gravity = Gravity.CENTER; setPadding(0, dp(4), 0, 0) })
    }

    private fun makeButton(text: String, color: Int, textColor: Int, action: () -> Unit) = Button(this).apply {
        this.text = text; textSize = 11f; typeface = Typeface.DEFAULT_BOLD; setTextColor(textColor)
        background = rounded(color, 14); isAllCaps = false; setOnClickListener { action() }
        stateListAnimator = null; minHeight = 0; minimumHeight = 0; minWidth = 0; minimumWidth = 0
    }

    private fun label(text: String, size: Float, color: Int, bold: Boolean) = TextView(this).apply {
        this.text = text; textSize = size; setTextColor(color); if (bold) typeface = Typeface.DEFAULT_BOLD
    }
    private fun rounded(color: Int, radius: Int) = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
    private fun space(height: Int) = View(this).apply { layoutParams = LinearLayout.LayoutParams(1, dp(height)) }
    private fun spaceWidth(width: Int) = View(this).apply { layoutParams = LinearLayout.LayoutParams(dp(width), 1) }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdownNow()
    }
}
