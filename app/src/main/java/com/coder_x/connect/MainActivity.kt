package com.coder_x.connect

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.coder_x.connect.databinding.ActivityMainBinding
import com.coder_x.connect.databinding.CustomToolbarBinding

// فئة النشاط الرئيسي التي تدير واجهة المستخدم وتفاعلاتها
class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(baseContext: Context) {
        super.attachBaseContext(LocaleHelper.getLocalizedContext(baseContext))
    }

    // ربط متغيرات تخطيط النشاط الرئيسي
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbarBinding: CustomToolbarBinding
    private lateinit var prefsHelper: SharedPrefsHelper

    // دالة تُستدعى عند إنشاء النشاط
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefsHelper = SharedPrefsHelper(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // ربط تخطيط شريط الأدوات المخصص

        toolbarBinding = CustomToolbarBinding.bind(binding.toolbar.root)


        // زر تغيير اللغة
        binding.toolbar.langIco.setOnClickListener {
            val currentLang = getCurrentLanguage()   // تحديد اللغة الجديدة
            val newLanguage = if (currentLang == "en") "ar" else "en"
            LocaleHelper.setLocale(this, newLanguage)   // تطبيق اللغة
            saveLanguagePreference(newLanguage)    // حفظ اللغة
            recreate()   // إعادة تحميل النشاط
        }


        // ستيراد بيانات الموظف المحفوظه أستخدام SharedPreference
        val name = prefsHelper.getEmpName()
        val department = prefsHelper.getEmpDepart()
        val id = prefsHelper.getEmpID()

        binding.empName.text = name
        binding.empDepart.text = department
        binding.empIdTxt.text = "$id"

        // استرجاع صورة الموظف
        val empImgPath = prefsHelper.getEmpImagePath()
        if (empImgPath != null) {
            try {
                // تحويل URI إلى Bitmap
                val imageUri = Uri.parse(empImgPath)
                binding.employeeImage.setImageURI(imageUri)


            } catch (e: Exception) {
                // معالجة الأخطاء المحتملة
                Log.e("MainActivity", "Error loading image: ${e.message}")
                // يمكنك وضع صورة افتراضية في حالة حدوث خطأ
                binding.employeeImage.setImageResource(R.drawable.emp_img)
            }
        }

        // إنشاء تأثير Ripple برمجيًا وتحديد لونه
        val rippleColor = Color.parseColor("#80FFFFFF")
        RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            ColorDrawable(Color.BLUE),  // لون خلفية الزر الافتراضي
            null
        )

        // تعيين مستمع النقر على زر تسجيل الدخول
        binding.checkInButton.setOnClickListener {
            animateButton(it)

        }
        // تعيين مستمع النقر على زر تسجيل الخروج
        binding.checkOutButton.setOnClickListener {
            animateButton(it)
            // اختبار الاتصال بقاعدة البيانات عند تسجيل الخروج
        }
        // تبديل الثيم بين النهاري والداكن عند النقر على أيقونة الثيم
        binding.toolbar.themeIco.setOnClickListener {
            val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            recreate()
        }

    }

    // دالة للحصول على اللغة الحالية
    private fun getCurrentLanguage(): String {
        val prefsHelper = SharedPrefsHelper(this)
        return prefsHelper.getLanguage()
    }

    // دالة لحفظ اللغة الجديدة
    private fun saveLanguagePreference(language: String) {
        val prefsHelper = SharedPrefsHelper(this)
        prefsHelper.setLanguage(language)
    }

    private fun animateButton(view: View) {
        val scaleAnimation = ScaleAnimation(
            // تعريف مقياس الزر عند النقر
            1f, 1.2f, 1f, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 200
        scaleAnimation.repeatCount = 1
        scaleAnimation.repeatMode = Animation.REVERSE
        // تعريف تأثير التلاشي
        val alphaAnimation = AlphaAnimation(0.2f, 1f)
        alphaAnimation.duration = 500
        // دمج التأثيرين في مجموعة واحدة
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(scaleAnimation)
        animationSet.addAnimation(alphaAnimation)
        // تطبيق التأثير على الزر
        view.startAnimation(animationSet)
    }
}