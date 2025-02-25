package com.coder_x.connect

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.coder_x.connect.databinding.ActivityMainBinding
import com.coder_x.connect.databinding.CustomToolbarBinding
import kotlin.concurrent.thread

// فئة النشاط الرئيسي التي تدير واجهة المستخدم وتفاعلاتها
class MainActivity : AppCompatActivity() {

    // ربط متغيرات تخطيط النشاط الرئيسي
    private lateinit var binding: ActivityMainBinding
    // ربط متغيرات تخطيط شريط الأدوات المخصص
    private lateinit var toolbarBinding: CustomToolbarBinding

    // دالة تُستدعى عند إنشاء النشاط
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // تفعيل ViewBinding وربط تخطيط النشاط
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ربط تخطيط شريط الأدوات المخصص
        toolbarBinding = CustomToolbarBinding.bind(binding.toolbar.root)

        // إنشاء تأثير Ripple برمجيًا وتحديد لونه
        val rippleColor = Color.parseColor("#80FFFFFF")
        val rippleDrawable = RippleDrawable(
            ColorStateList.valueOf(rippleColor),
            ColorDrawable(Color.BLUE),  // لون خلفية الزر الافتراضي
            null
        )

        // تعيين مستمع النقر على زر تسجيل الدخول
        binding.checkInButton.setOnClickListener {
            animateButton(it)
            // تحديث البيانات المعروضة
            binding.empName.text = "Mohamed Mustafa"
            binding.empDepart.text = "Hello"
            binding.empIdTxt.text = "#1000123"
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
        // تغيير اللغة بين العربية والإنجليزية عند النقر على أيقونة اللغة
        binding.toolbar.langIco.setOnClickListener {
            // تحديد اللغة الجديدة وعرضها
            val newLanguage = if (getCurrentLanguage() == "en") "ar" else "en"
            LocaleHelper.setLocale(this, newLanguage)
            saveLanguagePreference(newLanguage) // حفظ اللغة المختارة
            recreate() // إعادة تشغيل النشاط لتحديث اللغة
        }

    }

    // دالة للحصول على اللغة الحالية من الـ SharedPreferences
    private fun getCurrentLanguage(): String {
        // استرجاع اللغة من الإعدادات، القيمة الافتراضية هي الإنجليزية
        return getSharedPreferences("Settings", MODE_PRIVATE)
            .getString("My_Lang", "en") ?: "en"
    }

    // دالة لحفظ اللغة الجديدة في الـ SharedPreferences
    private fun saveLanguagePreference(language: String) {
        // حفظ اللغة في الإعدادات
        val editor = getSharedPreferences("Settings", MODE_PRIVATE).edit()
        editor.putString("My_Lang", language)
        editor.apply()
    }

    private fun animateButton(view: View) {
        val scaleAnimation = ScaleAnimation(
            // تعريف مقياس الزر عند النقر
            1f, 1.2f, 1f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
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

    // دالة لتطبيق تأثير الموجات على الزر
//    private fun applyAnimation(button: Button) {
//        // إضافة مستمع النقر على الزر
//        button.setOnClickListener {
//            // إنشاء تأثيرات التحجيم
//            val scaleX = ObjectAnimator.ofFloat(button, View.SCALE_X, 1f, 1.5f, 0.9f, 1.3f, 1f)
//            // إنشاء تأثيرات التحجيم
//            val scaleY = ObjectAnimator.ofFloat(button, View.SCALE_Y, 1f, 1.5f, 0.9f, 1.3f, 1f)
//
//            scaleX.duration = 800
//            scaleY.duration = 800
//            scaleX.interpolator = AccelerateDecelerateInterpolator()
//            scaleY.interpolator = AccelerateDecelerateInterpolator()
//
//            // دمج التأثيرين
//            val animatorSet = AnimatorSet()
//            animatorSet.playTogether(scaleX, scaleY)
//            animatorSet.start()
//            // الحصول على تأثير الـ Ripple من خلفية الزر
//            // تشغيل تأثير الـ Ripple يدويًا مباشرةً
//            val rippleDrawable = button.background as? RippleDrawable
//            rippleDrawable?.setHotspot(button.width / 2f, button.height / 2f)
//            button.isPressed = true
//            button.invalidate() // يجبر النظام على إعادة رسم التأثير
//            button.postDelayed(
//                { button.isPressed = false },
//                200
//            ) // يزيل الضغط بعد 200ms لجعل التأثير طبيعيًا
//        }
//    }


}