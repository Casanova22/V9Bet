package com.app.v9bet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.v9bet.databinding.WebviewActivityBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import database.FirebaseDB

@Suppress("DEPRECATION")
class WebViewActivity : AppCompatActivity() {

    private var fbDataBase = FirebaseDB()
    private lateinit var binding: WebviewActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WebviewActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getLink()
        init()
    }

    private fun getLink() {
        fbDataBase.getDatabase().addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged", "SetJavaScriptEnabled")
            override fun onDataChange(snapshot: DataSnapshot) {

                for (ds in snapshot.children) {
                    val pn = ds.child("PackageName").getValue(String::class.java)
                    val url = ds.child("Url").getValue(String::class.java)
                    val status = ds.child("Status").getValue(Int::class.java)
                    Log.d("TAG", "$pn / $url")

                    val webView : WebView = binding.webView

                    if (this@WebViewActivity.packageName == pn){
                        if (status == 0){
                            webView.loadUrl(url.toString())
                        }   else {
                            webView.loadUrl("https://wanli2.vip/?c=p00006")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {

        with(binding.webView) {
            with(settings) {
                javaScriptEnabled = true
                defaultTextEncodingName = "UTF-8"
                cacheMode = WebSettings.LOAD_NO_CACHE
                useWideViewPort = true
                pluginState = WebSettings.PluginState.ON
                domStorageEnabled = true
                builtInZoomControls = false
                layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
                loadWithOverviewMode = true
                blockNetworkImage = true
                loadsImagesAutomatically = true
                setSupportZoom(false)
                setSupportMultipleWindows(true)
            }
            requestFocusFromTouch()
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        }

        val webseting: WebSettings = binding.webView.settings
        with(webseting) {
            val appCacheDir = this@WebViewActivity.getDir(
                "cache", AppCompatActivity.MODE_PRIVATE
            )?.path
            domStorageEnabled = true
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_DEFAULT
        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, newProgress: Int) {
                binding.pbLoading.progress = newProgress
                if (newProgress == 100) {
                    binding.webView.settings.blockNetworkImage = false
                }
            }

            override fun onCreateWindow(
                view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message
            ): Boolean {
                val newWebView = WebView(this@WebViewActivity)
                val transport = resultMsg.obj as WebView.WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                newWebView?.webViewClient = object : WebViewClient() {
                    @Deprecated("Deprecated in Java")
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        binding.webView.loadUrl(url)
                        if (url.startsWith("http") || url.startsWith("https")) {
                            return super.shouldOverrideUrlLoading(view, url)
                        } else if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith(WebView.SCHEME_MAILTO)) {
                            val dialIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(dialIntent)
                        } else {
                            try {
                                val `in` = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                this@WebViewActivity.startActivity(`in`)

                            } catch (ex: ActivityNotFoundException) {
                                val makeShortText = "The Application has not been installed"
                                Toast.makeText(this@WebViewActivity, makeShortText, Toast.LENGTH_SHORT).show()
                            }
                        }
                        return true
                    }
                }
                return true
            }
        }

        val settings: WebSettings = binding.webView.settings
        settings.javaScriptEnabled = true
        binding.webView.setOnLongClickListener { v: View ->
            val result = (v as WebView).hitTestResult
            val type = result.type
            if (type == WebView.HitTestResult.UNKNOWN_TYPE) return@setOnLongClickListener false
            when (type) {
                WebView.HitTestResult.PHONE_TYPE -> {}
                WebView.HitTestResult.EMAIL_TYPE -> {}
                WebView.HitTestResult.GEO_TYPE -> {}
                WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE -> {}

                else -> {}
            }
            true
        }

        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                binding.pbLoading.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.pbLoading.visibility = View.GONE
            }

            override fun onReceivedError(
                view: WebView, request: WebResourceRequest, error: WebResourceError
            ) {
                super.onReceivedError(view, request, error)
            }

            @SuppressLint("WebViewClientOnReceivedSslError")
            override fun onReceivedSslError(
                view: WebView, handler: SslErrorHandler, error: SslError
            ) {
                val builder = AlertDialog.Builder(this@WebViewActivity)
                var message = "SSL Certificate error."
                when (error.primaryError) {
                    SslError.SSL_UNTRUSTED -> message = "The certificate authority is not trusted."
                    SslError.SSL_EXPIRED -> message = "The certificate has expired."
                    SslError.SSL_IDMISMATCH -> message = "The certificate Hostname mismatch."
                    SslError.SSL_NOTYETVALID -> message = "The certificate is not yet valid."
                }
                message += " Do you want to continue anyway?"
                builder.setTitle("SSL Certificate Error")
                builder.setMessage(message)
                builder.setPositiveButton(
                    "Continue"
                ) { _: DialogInterface?, _: Int -> handler.proceed() }
                builder.setNegativeButton(
                    "Cancel"
                ) { _: DialogInterface?, _: Int -> handler.cancel() }
                val dialog = builder.create()
                dialog.show()
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith("http") || url.startsWith("https")) {
                    return super.shouldOverrideUrlLoading(view, url)
                } else if (url.startsWith("intent:")) {
                    val urlSplit = url.split("/").toTypedArray()
                    var send = ""
                    if (urlSplit[2] == "user") {
                        send = "https://m.me/" + urlSplit[3]
                    } else if (urlSplit[2] == "ti") {
                        val data = urlSplit[4]
                        val newSplit = data.split("#").toTypedArray()
                        send = "https://line.me/R/" + newSplit[0]
                    }
                    val newInt = Intent(Intent.ACTION_VIEW, Uri.parse(send))
                    this@WebViewActivity.startActivity(newInt)
                } else {
                    try {
                        val `in` = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        this@WebViewActivity.startActivity(`in`)
                    } catch (ex: ActivityNotFoundException) {
                        val makeShortText = "The Application has not been installed"
                        Toast.makeText(this@WebViewActivity, makeShortText, Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }
        }

        binding.webView.setOnKeyListener { _: View?, i: Int, keyEvent: KeyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN) {
                if (i == KeyEvent.KEYCODE_BACK && binding.webView.canGoBack()) {
                    binding.webView.goBack()
                    return@setOnKeyListener true
                }
            }
            false
        }
    }
}