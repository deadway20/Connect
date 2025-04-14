package com.coder_x.connect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coder_x.connect.databinding.ActivityEditServerSettingBinding

class ServerSettingActivity : AppCompatActivity() {
    private lateinit var prefsHelper: SharedPrefsHelper
    private lateinit var binding: ActivityEditServerSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditServerSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}