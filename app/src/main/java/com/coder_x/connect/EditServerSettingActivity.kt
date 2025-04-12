package com.coder_x.connect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coder_x.connect.databinding.ActivityEditProfileBinding

class EditServerSettingActivity : AppCompatActivity() {
    private lateinit var prefsHelper: SharedPrefsHelper
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}