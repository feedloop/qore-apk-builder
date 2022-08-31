package com.example.core.utils

import android.app.Activity
import android.content.Intent
import android.webkit.WebView
import androidx.fragment.app.FragmentManager
import id.amoda.MainActivity

interface WebViewHelper {
    fun getWebView(): WebView
    fun activity(): Activity
    fun supportFragmenManager(): FragmentManager
}

object WebViewDataHelper {
    var listener: WebViewHelper? = null

    fun init(newListener: WebViewHelper) {
        listener = newListener
    }

    fun openWebview(
        activity: Activity,
        url: String
    ) {
        MainActivity.url = url
        MainActivity.enableOverrideUrlLoading = true
        activity.startActivity(Intent(activity, MainActivity::class.java))
    }

}