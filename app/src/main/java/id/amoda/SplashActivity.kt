package id.amoda

import android.R
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.core.utils.WebViewDataHelper
import id.amoda.databinding.ActivitySplashBinding
import java.util.*
import kotlin.concurrent.schedule


class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val window: Window = this.window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.holo_orange_light)

        Timer("Fetch config first", false).schedule(2000) {
            WebViewDataHelper.openWebview(this@SplashActivity, "https://amoda-apps.qore.dev")
            finish()
        }

    }

    override fun onBackPressed() {
        finish()
    }
}