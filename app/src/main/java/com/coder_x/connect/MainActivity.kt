package com.coder_x.connect

import android.content.Context
import android.content.Intent
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
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
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


        prefsHelper = SharedPrefsHelper(this)
        val myTheme = prefsHelper.getTheme()
        if (myTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }


        setContentView(binding.root)
        toolbarBinding = CustomToolbarBinding.bind(binding.topBar.root)

        prefsHelper = SharedPrefsHelper(this)


        getCurrentDate()

        // تحميل البيانات من السيرفر
        getDataFromServer()
        // تعيين مستمع النقر على زر تغيير اللغة
        setLangIconClickListener(binding.topBar.langIco)
        // ستيراد بيانات الموظف المحفوظه أستخدام SharedPreference
        displayEmpInfo(
            prefsHelper.getEmpName(),
            prefsHelper.getEmpImagePath()
        )

        val bottomBarBackground = binding.bottomNavigation.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel =
            bottomBarBackground.shapeAppearanceModel.toBuilder().apply {
                setAllCorners(CornerFamily.ROUNDED, 75f)
            }.build()

        // تعيين مستمع النقر على زر تسجيل الدخول
        checkInButton(binding.clockInBtn)


        // تعيين مستمع النقر على زر تسجيل الخروج
        checkOutButton(binding.clockOutBtn)

        // كارت تعيين مستمع النقر على كارت أيام الحضور
        animateAttendCard(binding.attendanceCard)

        binding.employeeImage.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        

    }

    private fun animateAttendCard(view: View) {
        view.setOnClickListener {
            animateCard(binding.attendanceCard)
            val intent = Intent(this, Calendar::class.java)
            startActivity(intent)
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

        val alphaAnimation = AlphaAnimation(.5f, 0.3f)
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
        binding.clockIn.text = "--:--"
        binding.clockOut.text = "--:--"
        binding.delaysHours.text = "--:--"
        binding.overtimeHours.text = "--:--"
    }

    private fun displayEmpInfo(name: String, empImgPath: String?) {
        val currentTime = LocalTime.now()
        val morning = LocalTime.of(6, 0) // 6:00 AM
        val evening = LocalTime.of(12, 0) // 12:00 PM
        val night = LocalTime.of(18, 0) // 6:00 PM
        val nightGreeting = resources.getString(R.string.night_greeting)
        val morningGreeting = resources.getString(R.string.morning_greeting)
        val eveningGreeting = resources.getString(R.string.evening_greeting)

        binding.greeting.text = when {
            currentTime.isAfter(night) -> nightGreeting
            currentTime.isAfter(evening) -> eveningGreeting
            else -> morningGreeting
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
    private fun displayAttendanceData(data: EmployeeData) {
        binding.clockIn.text = data.clockIn
        binding.clockOut.text = data.clockOut
        binding.totalHours.text = data.totalHours
        binding.delaysHours.text = data.delayInMinutes
        binding.overtimeHours.text = data.overtimeInMinutes
        binding.attendDaysCount.text = data.attendCount.toString()
        binding.absenceDaysCount.text = data.absenceCount.toString()

        Log.d("My log", "data: $data ")
    }

    // دالة لتعيين صورة الخلفية
    private fun getCurrentDate() {
        // تحديث الوقت كل ثانية
        CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                binding.timeDisplay.text =
                    SimpleDateFormat("hh:mm:ss a").format(System.currentTimeMillis())
                delay(1000)
            }
        }

        // تعيين التاريخ الحالي
        binding.dateDisplay.text =
            SimpleDateFormat("EEEE dd - MMMM - yyyy").format(System.currentTimeMillis())
    }
}