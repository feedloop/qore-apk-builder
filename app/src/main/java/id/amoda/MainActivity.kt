package id.amoda

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.FragmentManager
import com.example.core.utils.WebViewDataHelper
import com.example.core.utils.WebViewHelper
import id.amoda.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), WebViewHelper {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebViewDataHelper.init(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initWebView()

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
            settings.domStorageEnabled = true
            WebView.setWebContentsDebuggingEnabled(true)

            jsInterface?.let {
                addJavascriptInterface(it, "nativeApp")
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    binding.webviewLoadIndicator.visibility = View.VISIBLE
                    binding.frameWebView.visibility = View.GONE
                    if (newProgress == 100) {
                        binding.webviewLoadIndicator.visibility = View.GONE
                        binding.frameWebView.visibility = View.VISIBLE
                    }
                }
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