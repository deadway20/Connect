package com.coder_x.connect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import com.coder_x.connect.databinding.ActivityProfileBinding


class ProfileActivity : AppCompatActivity() {

    override fun attachBaseContext(baseContext: Context) {
        super.attachBaseContext(LocaleHelper.getLocalizedContext(baseContext))
    }

    private lateinit var binding: ActivityProfileBinding
    private lateinit var prefsHelper: SharedPrefsHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPrefsHelper(this)


        val clickableViews = listOf(binding.editProfileArrow, binding.editProfileText)
        clickableViews.forEach { view ->
            view.setOnClickListener {
                startActivity(Intent(this, EditProfileActivity::class.java))
            }
        }
        val clickableViews2 = listOf(binding.serverSettingArrow, binding.serverSettingText)
        clickableViews2.forEach { view ->
            view.setOnClickListener {
                startActivity(Intent(this, ServerSettingActivity::class.java))
            }
        }




        viewDirection(binding.view)

        // استرجاع صورة الموظف
        val imagePath = prefsHelper.getEmpImagePath()
        if (imagePath != null) {
            try {
                // تحويل URI إلى Bitmap
                val imageUri = imagePath.toUri()
                binding.employeeImage.setImageURI(imageUri)


            } catch (e: Exception) {
                // معالجة الأخطاء المحتملة
                Log.e("MainActivity", "Error loading image: ${e.message}")
                // يمكنك وضع صورة افتراضية في حالة حدوث خطأ
                binding.employeeImage.setImageResource(R.drawable.emp_img)
            }
        }

        binding.employeeName.text = prefsHelper.getEmpName()
        binding.profileId.text = prefsHelper.getEmpID().toString()
        binding.departmentName.text = prefsHelper.getEmpDepartment()

        // التحقق من وضعية المفتاح عند تشغيل التطبيق
        val isDarkMode = prefsHelper.getTheme()
        binding.DarkModeSwitch.isChecked = isDarkMode

        // حفظ وضعية المفتاح عند تغييره
        binding.DarkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                prefsHelper.setTheme(true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                prefsHelper.setTheme(false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }




        ArrayAdapter.createFromResource(
            this,
            R.array.language_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.languageSpinner.adapter = adapter
        }

    }


    private fun viewDirection(view: View) {
        prefsHelper = SharedPrefsHelper(this)
        if (prefsHelper.getLanguage() == "en") {
            view.rotation = -20f
            view.pivotX = -50f
            view.pivotY = 80f

        } else {
            view.rotation = 20f
            view.pivotX = 1500f
            view.pivotY = 80f
        }
    }
}