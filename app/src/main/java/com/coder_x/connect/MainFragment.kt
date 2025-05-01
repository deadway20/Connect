package com.coder_x.connect

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.coder_x.connect.DataBaseHelper.checkInEmployee
import com.coder_x.connect.DataBaseHelper.checkOutEmployee
import com.coder_x.connect.databinding.FragmentMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime

class MainFragment : Fragment() {
    private val prefsHelper by lazy { SharedPrefsHelper(requireContext()) }
    private lateinit var binding: FragmentMainBinding
    private var dataFetchJob: Job? = null
    override fun onAttach(context: Context) {
        super.onAttach(LocaleHelper.getLocalizedContext(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        applyTheme()
        getCurrentDate()
        getDataFromServer()
        applyTheme()
        displayEmpInfo(prefsHelper.getEmployeeName(), prefsHelper.getEmployeeImageUri())
        checkInButton(binding.clockInBtn)
        checkOutButton(binding.clockOutBtn)
        return binding.root
    }

    private fun checkInButton(view: View) {
        view.setOnClickListener {
            animateButton(it) // تشغيل الأنيميشن عند الضغط
            checkInEmployee(requireContext()) { success, message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                if (success) {
                    getDataFromServer()
                }
            }
        }
    }

    // track check out Time button
    private fun checkOutButton(view: View) {
        view.setOnClickListener {
            animateButton(it)

            checkOutEmployee(requireContext()) { success, message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                if (success) {
                    getDataFromServer()
                }
            }
        }
    }

    // make button animation
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

    // Get data from server
    private fun getDataFromServer() {
        dataFetchJob?.cancel()

        dataFetchJob = viewLifecycleOwner.lifecycleScope.launch {
            if (!isAdded) return@launch

            val context = context ?: return@launch
            val currentDate = LocalDate.now()
            val savedData = prefsHelper.getSavedData()

            if (savedData != null && savedData.recordDate == currentDate.toString()) {
                displayAttendanceData(savedData)
            } else {
                prefsHelper.clearData()
                clearUi()

                try {
                    DataBaseHelper.getAttendanceData(context) { data ->
                        if (isAdded) {
                            if (data != null) {
                                displayAttendanceData(data)
                            } else {
                                Toast.makeText(context, "❌ لم يتم الحضور اليوم!", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    if (isAdded) {
                        Toast.makeText(
                            context,
                            "❌ فشل في جلب البيانات: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    // Clear UI elements
    private fun clearUi() {
        // display default data in UI
        binding.clockIn.text = "--:--"
        binding.clockOut.text = "--:--"
        binding.delaysHours.text = "--:--"
        binding.overtimeHours.text = "--:--"
    }

    // displaying employee information
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
        if (!firstName.isNullOrEmpty()) {
            binding.empName.text = firstName
        }

        Log.d("My log", "Language: ${prefsHelper.getLanguage()}")
        if (prefsHelper.getLanguage() == "ar") {
            binding.quotesText.text = QuotesList.arabicQuotes.random()
        } else {
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

    // displaying attendance data
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

    // displaying current date and time
    @SuppressLint("SimpleDateFormat")
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

    // applying the theme
    private fun applyTheme() {
        val myTheme = prefsHelper.getTheme()
        if (myTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onDestroyView() {
        dataFetchJob?.cancel()
        dataFetchJob = null
        super.onDestroyView()
    }

}