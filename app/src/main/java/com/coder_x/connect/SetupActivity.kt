package com.coder_x.connect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coder_x.connect.databinding.ActivitySetupBinding

class SetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // إعداد الـ ViewPager
        val adapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false // تعطيل السحب اليدوي بين الصفحات
    }

    // دالة للانتقال إلى الصفحة التالية
    fun nextPage() {
        binding.viewPager.currentItem += 1
    }

    // دالة للانتقال إلى الصفحة السابقة
    @Suppress("unused")
    fun prevPage() {
        binding.viewPager.currentItem -= 1
    }
}
