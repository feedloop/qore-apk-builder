package id.amoda

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.example.core.utils.WebViewDataHelper
import com.example.core.utils.WebViewHelper
import id.amoda.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), WebViewHelper {
    private lateinit var binding: ActivityMainBinding

    private val permission = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
    private val requestCode = 1

    private val FILECHOOSER_RESULTCODE = 1

    private var mUploadMessage: ValueCallback<Array<Uri?>?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebViewDataHelper.init(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isPermissionGranted()) {
            askPermissions()
        }
        initWebView()
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(this, permission, requestCode)
    }

    private fun isPermissionGranted(): Boolean {
        permission.forEach {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED)
                return false
        }

        return true
    }

    internal inner class MyWebChromeClient : WebChromeClient() {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onPermissionRequest(request: PermissionRequest) {
            request.grant(request.resources)
        }

        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            binding.webviewLoadIndicator.visibility = View.VISIBLE
            binding.frameWebView.visibility = View.GONE
            if (newProgress == 100) {
                binding.webviewLoadIndicator.visibility = View.GONE
                binding.frameWebView.visibility = View.VISIBLE
            }
        }

        // maneja la accion de seleccionar archivos
        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri?>?>,
            fileChooserParams: FileChooserParams?
        ): Boolean {

            // asegurar que no existan callbacks
            if (mUploadMessage != null) {
                try {
                    mUploadMessage!!.onReceiveValue(null)
                } catch (e: Exception) {

                }
            }
            mUploadMessage = filePathCallback
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "image/*" // set MIME type to filter
            this@MainActivity.startActivityForResult(
                Intent.createChooser(i, "File Chooser"),
                FILECHOOSER_RESULTCODE
            )
            return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        // manejo de seleccion de archivo
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage || intent == null || resultCode != RESULT_OK) {
                return
            }
            var result: Array<Uri?>? = null
            val dataString = intent.dataString
            if (dataString != null) {
                result = arrayOf(Uri.parse(dataString))
            }
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        Log.d("WebView load ", url)
        binding.webviewLoadIndicator.visibility = View.VISIBLE
        binding.frameWebView.visibility = View.GONE
        binding.webviewContent.apply {
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.builtInZoomControls = false
            settings.userAgentString = WebSettings.getDefaultUserAgent(this.context)
            settings.databaseEnabled = true
            settings.domStorageEnabled = true
            settings.setGeolocationEnabled(true)
            settings.javaScriptEnabled = true
            isVerticalScrollBarEnabled = true
            settings.allowContentAccess = true
            settings.setAppCacheEnabled(false)
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            webChromeClient = MyWebChromeClient()
            WebView.setWebContentsDebuggingEnabled(true)

            jsInterface?.let {
                addJavascriptInterface(it, "nativeApp")
            }

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    binding.webviewLoadIndicator.visibility = View.GONE
                    binding.frameWebView.visibility = View.VISIBLE
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return if (enableOverrideUrlLoading) {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(url)
                        )
                        startActivity(intent)
                        true
                    } else false
                }
            }
            loadUrl(MainActivity.url)
        }
    }

    override fun onBackPressed() {
        val backPageEnabled = intent.getBooleanExtra(enableBackPage, true)
        if (getWebView().canGoBack() && backPageEnabled) {
            getWebView().goBack()
        } else finishAffinity()
    }

    override fun getWebView(): WebView = binding.webviewContent

    override fun activity(): Activity = this

    override fun supportFragmenManager(): FragmentManager = supportFragmentManager

    companion object {
        var enableOverrideUrlLoading = false
        var url: String = "https://amoda-apps.qore.dev/"
        var jsInterface: Any? = null
        var enableBackPage = "ENABLE_BACK_PAGE"
    }
}