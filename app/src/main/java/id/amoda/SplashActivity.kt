package id.amoda

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

        Timer("Fetch config first", false).schedule(2000) {
            WebViewDataHelper.openWebview(this@SplashActivity, "https://amoda-apps.qore.dev")
            finish()
        }

    }

    override fun onBackPressed() {
        finish()
    }
}