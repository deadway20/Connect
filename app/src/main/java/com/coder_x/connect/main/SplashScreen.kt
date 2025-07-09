package com.coder_x.connect.main

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.coder_x.connect.databinding.SplashScreenBinding
import com.coder_x.connect.helpers.SharedPrefsHelper
import com.coder_x.connect.onboarding.SetupActivity

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: SplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefsHelper = SharedPrefsHelper(this)
        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lottieConnect.apply {
            setAnimation(com.coder_x.connect.R.raw.lottie_connect)
            speed = 1.65f
            playAnimation()
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    lifecycleScope.launchWhenResumed {
                        if (prefsHelper.getSetupCompleted()) {
                            startActivity(Intent(this@SplashScreen, BottomBarActivity::class.java))
                        } else {
                            startActivity(Intent(this@SplashScreen, SetupActivity::class.java))
                        }
                        finish()
                    }
                }

                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }



//        Glide.with(this)
//            .asGif()
//            .load(R.drawable.splash_screen)
//            .into(binding.logo)

//        Handler(Looper.getMainLooper()).postDelayed({
//
//            if (prefsHelper.getSetupCompleted()) {
//                startActivity(Intent(this, BottomBarActivity::class.java))
//            } else {
//                startActivity(Intent(this, SetupActivity::class.java))
//            }
//            finish()
//        }, 2500)

    }

}
