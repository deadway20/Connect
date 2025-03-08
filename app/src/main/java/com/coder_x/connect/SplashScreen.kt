package com.coder_x.connect

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.coder_x.connect.databinding.SplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: SplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefsHelper = SharedPrefsHelper(this)
        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .asGif() // تأكد أن الصورة يتم تحميلها كـ GIF
            .load(R.drawable.splash_screen)
            .into(binding.logo)


        // تطبيق اللغة المحفوظة
        val savedLanguage = prefsHelper.getLanguage()
        LocaleHelper.setLocale(this, savedLanguage)

        Handler().postDelayed({

            if (prefsHelper.getSetupCompleted()) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, SetupActivity::class.java))
            }
            finish()
        }, 2000)
    }

    override fun attachBaseContext(baseContext: Context) {
        super.attachBaseContext(LocaleHelper.getLocalizedContext(baseContext))
    }
}
