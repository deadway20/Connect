package com.coder_x.connect

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import com.coder_x.connect.DataBaseHelper.checkInEmployee
import com.coder_x.connect.DataBaseHelper.checkOutEmployee
import com.coder_x.connect.databinding.ActivityMainBinding
import com.coder_x.connect.databinding.CustomToolbarBinding
import java.time.LocalDate
import java.time.LocalTime

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toolbarBinding = CustomToolbarBinding.bind(binding.toolbar.root)

        prefsHelper = SharedPrefsHelper(this)

        //change wallpaper when shake the phone
        var background = listOf(
            R.drawable.background,
            R.drawable.backgroung1,
            R.drawable.backgroung10,
            R.drawable.backgroung11,
            R.drawable.backgroung2,
            R.drawable.backgroung3,
            R.drawable.backgroung4,
            R.drawable.backgroung5,
            R.drawable.backgroung6,
            R.drawable.backgroung7,
            R.drawable.backgroung8,
            R.drawable.backgroung9,
        )
        binding.root.setBackgroundResource(background.random())

        // تحميل البيانات من السيرفر
        getDataFromServer()
        // تعيين مستمع النقر على زر تغيير اللغة
        setLangIconClickListener(binding.toolbar.langIco)
        // ستيراد بيانات الموظف المحفوظه أستخدام SharedPreference
        displayEmpInfo(
            prefsHelper.getEmpName(),
            prefsHelper.getEmpImagePath()
        )


        // تعيين مستمع النقر على زر تسجيل الدخول
        checkInButton(binding.checkInButton)


        // تعيين مستمع النقر على زر تسجيل الخروج
        checkOutButton(binding.checkOutButton)

        // كارت تعيين مستمع النقر على كارت أيام الحضور
        animateAttendCard(binding.attendDays)

        // تبديل الثيم بين النهاري والداكن
        setTheme(binding.toolbar.themeIco)

    }

    private fun animateAttendCard(view: View) {
        view.setOnClickListener {
            animateCard(binding.attendDays)
            val dialogFragment = CalendarFragment()
            dialogFragment.show(supportFragmentManager, "calendar")
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

    private fun setLangIconClickListener(view: View) {
        view.setOnClickListener {
            // زر تغيير اللغة
            val currentLang = getCurrentLanguage()   // تحديد اللغة الجديدة
            val newLanguage = if (currentLang == "en") "ar" else "en"
            LocaleHelper.setLocale(this, newLanguage)   // تطبيق اللغة
            saveLanguagePreference(newLanguage)    // حفظ اللغة
            recreate()   // إعادة تحميل النشاط
        }
    }

    private fun checkInButton(view: View) {
        view.setOnClickListener {
            animateButton(it) // تشغيل الأنيميشن عند الضغط
            checkInEmployee(this) { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                if (success) {
                    getDataFromServer()
                }
            }
        }
    }

    private fun checkOutButton(view: View) {
        view.setOnClickListener {
            animateButton(it)

            checkOutEmployee(this) { success, message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                if (success) {
                    getDataFromServer()
                }
            }
        }
    }

    private fun setTheme(view: View) {
        view.setOnClickListener {
            val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            recreate()
        }
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

    private fun animateCard(view: View) {
        val scaleAnimation = ScaleAnimation(
            1f, 1.1f, 1f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnimation.duration = 100
        scaleAnimation.repeatCount = 1
        scaleAnimation.repeatMode = Animation.REVERSE

        val alphaAnimation = AlphaAnimation(1f, 0.8f)
        alphaAnimation.duration = 100
        alphaAnimation.repeatCount = 1
        alphaAnimation.repeatMode = Animation.REVERSE

        val animationSet = AnimationSet(false)
        animationSet.addAnimation(scaleAnimation)
        animationSet.addAnimation(alphaAnimation)
        // تطبيق التأثير على الزر
        view.startAnimation(animationSet)
    }

    // جلب البيانات من السيرفر بعد تحميل الصفحة
    private fun getDataFromServer() {
        // get today's date
        val currentDate = LocalDate.now()
        // Retrieve saved attendance data from SharedPrefsHelper
        val savedData = prefsHelper.getSavedData()

        if (savedData != null && savedData.recordDate == currentDate.toString()) {
            displayAttendanceData(savedData)
        } else {
            // No data or data is not for today, clear UI and fetch from server
            prefsHelper.clearData()
            clearUi()
            DataBaseHelper.getAttendanceData(this) { data ->
                if (data != null) {
                    displayAttendanceData(data)
                } else {
                    Toast.makeText(this, "❌ لم يتم الحضور اليوم!", Toast.LENGTH_LONG).show()

                }
            }
        }

        DataBaseHelper.getAttendanceData(this) { data ->
            if (data != null) {
                displayAttendanceData(data)
            } else {
                Toast.makeText(this, "❌ لم يتم الحضور اليوم!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearUi() {
        // display default data in UI
        binding.attendTime.text = "--:--"
        binding.departTime.text = "--:--"
        binding.delaysHours.text = "--:--"
        binding.overtimeHours.text = "--:--"
        binding.isAbsence.text = "✖"
        binding.isAttend.text = "✖"
    }

    private fun displayEmpInfo(name: String, empImgPath: String?) {
        val currentTime = LocalTime.now()
        val morning = LocalTime.of(6, 0) // 6:00 AM
        val evening = LocalTime.of(12, 0) // 12:00 PM
        val night = LocalTime.of(18, 0) // 6:00 PM
        val goodnight_greeting = resources.getString(R.string.night_greeting)
        val morning_greeting = resources.getString(R.string.morning_greeting)
        val evening_greeting = resources.getString(R.string.evening_greeting)

        binding.greeting.text = when {
            currentTime.isAfter(night) -> goodnight_greeting
            currentTime.isAfter(evening) -> evening_greeting
            else -> morning_greeting
        }
        binding.greetingIcon.text = when {
            currentTime.isAfter(night) -> resources.getString(R.string.night_icon)
            currentTime.isAfter(evening) -> resources.getString(R.string.evening_icon)
            currentTime.isAfter(morning) -> resources.getString(R.string.morning_icon)
            else -> resources.getString(R.string.morning_icon)
        }

        val firstName = name.split(" ").firstOrNull()
        if (!firstName.isNullOrEmpty()){
            binding.empName.text = firstName
        }

        if(prefsHelper.getLanguage() == "ar"){
            binding.quotesText.text = QuotesList.arabicQuotes.random()
        }else{
            binding.quotesText.text = QuotesList.englishQuotes.random()
        }
        // استرجاع صورة الموظف
        if (empImgPath != null) {
            try {
                // تحويل URI إلى Bitmap
                val imageUri = empImgPath.toUri()
                binding.employeeImage.setImageURI(imageUri)


            } catch (e: Exception) {
                // معالجة الأخطاء المحتملة
                Log.e("MainActivity", "Error loading image: ${e.message}")
                // يمكنك وضع صورة افتراضية في حالة حدوث خطأ
                binding.employeeImage.setImageResource(R.drawable.emp_img)
            }
        }
    }

    // دالة لعرض البيانات في الواجهة
    private fun displayAttendanceData(data: EmpData) {
        binding.attendTime.text = data.checkInTime
        binding.departTime.text = data.checkOutTime
        binding.delaysHours.text = data.delayInMinutes
        binding.overtimeHours.text = data.overtimeInMinutes
        binding.attendDaysCount.text = data.attendCount.toString()
        binding.absenceDaysCount.text = data.absenceCount.toString()
        when {
            data.isAbsence -> {
                binding.isAbsence.text = "✔"
                binding.isAttend.text = "✖"
            }
            else -> {
                binding.isAbsence.text = "✖"
                binding.isAttend.text = "✔"
            }
        }
        Log.d("My log", "data: $data ")
    }
}