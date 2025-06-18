package com.coder_x.connect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coder_x.connect.databinding.ActivityMainBinding


// فئة النشاط الرئيسي التي تدير واجهة المستخدم وتفاعلاتها
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding = ActivityMainBinding.inflate(layoutInflater)
    }

}