package com.coder_x.connect

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import com.coder_x.connect.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        animateLogo()
        return binding.root
    }

    private fun animateLogo() {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.2f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.2f, 1f)

        val animator = ObjectAnimator.ofPropertyValuesHolder(binding.logo, scaleX, scaleY)
        animator.duration = 1000
        animator.repeatCount = 2
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()

        binding.logo.postDelayed({
            (activity as? SetupActivity)?.nextPage()
        }, 3000)
    }
}
