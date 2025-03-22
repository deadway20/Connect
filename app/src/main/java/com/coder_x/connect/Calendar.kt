package com.coder_x.connect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coder_x.connect.databinding.ActivityCalendarBinding

class Calendar : AppCompatActivity() {
    private lateinit var binding: ActivityCalendarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}