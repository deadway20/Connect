package com.coder_x.connect.main

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.coder_x.connect.R
import com.coder_x.connect.database.NoteViewModel
import com.coder_x.connect.databinding.FragmentMainBinding
import com.coder_x.connect.helpers.DataBaseHelper
import com.coder_x.connect.helpers.DataBaseHelper.checkInEmployee
import com.coder_x.connect.helpers.DataBaseHelper.checkOutEmployee
import com.coder_x.connect.helpers.LocaleHelper
import com.coder_x.connect.helpers.SharedPrefsHelper
import com.coder_x.connect.model.EmployeeData
import com.coder_x.connect.social.QuotesList
import com.coder_x.connect.todo.TodoAdapter
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
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
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var notesAdapter: TodoAdapter // You'll need to create this adapter
    private var dataFetchJob: Job? = null
    override fun onAttach(context: Context) {
        super.onAttach(LocaleHelper.getLocalizedContext(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        binding = FragmentMainBinding.inflate(inflater, container, false)
        applyTheme()
        getCurrentDate()
        getDataFromServer()
        applyTheme()
        setupCharts()
        setupPieChart()
        displayGreetingMsg()
        checkInButton(binding.checkInAnimation)
        checkOutButton(binding.checkOutAnimation)
        displayActiveTasksCount()
        return binding.root
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
                            context, "❌ فشل في جلب البيانات: ${e.message}", Toast.LENGTH_LONG
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
    private fun displayGreetingMsg() {
        var empName = prefsHelper.getEmployeeName()
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

        val firstName = empName.split(" ").firstOrNull()
        if (!firstName.isNullOrEmpty()) {
            binding.empName.text = firstName
        }
        if (prefsHelper.getLanguage() == "ar") {
            binding.quotesText.text = QuotesList.arabicQuotes.random()
        } else {
            binding.quotesText.text = QuotesList.englishQuotes.random()
        }

        val empImg = prefsHelper.getEmployeeImageBitmap()
        if (empImg != null) {
            binding.employeeImage.setImageBitmap(empImg)
        }else {
            binding.employeeImage.setImageResource(R.drawable.emp_img)
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
    }

    private fun checkInButton(view: View) {
        view.setOnClickListener {
            // Ensure the view is LottieAnimationView before casting
            if (view is com.airbnb.lottie.LottieAnimationView && !view.isAnimating) {
                checkInEmployee(requireContext()) { success, message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    if (success) {
                        view.setAnimation(R.raw.lottie_check_in)
                        view.repeatCount = 0 // Don't repeat animation
                        view.playAnimation()
                        view.addAnimatorListener(object : Animator.AnimatorListener {
                            override fun onAnimationEnd(animation: Animator) {
                                getDataFromServer()
                            }

                            override fun onAnimationStart(animation: Animator) {}
                            override fun onAnimationCancel(animation: Animator) {}
                            override fun onAnimationRepeat(animation: Animator) {}
                        })
                    } else {
                        view.setAnimation(R.raw.chechin_error) // Corrected Lottie file name
                        view.playAnimation()
                    }
                }
            }
        }
    }




    // track check out Time button
    private fun checkOutButton(view: View) {
        view.setOnClickListener {
            // Ensure the view is LottieAnimationView before casting
            if (view is com.airbnb.lottie.LottieAnimationView && !view.isAnimating) {
                view.setAnimation(R.raw.lottie_check_out)
                view.repeatCount = 0 // Don't repeat animation
                view.playAnimation()
                checkOutEmployee(requireContext()) { success, message ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    if (success) {
                        getDataFromServer()
                    }
                }
            }
        }
    }

    fun displayActiveTasksCount(){
        noteViewModel.getActiveTasksCount().observe(viewLifecycleOwner) { activeNotesCount ->
            binding.uncompletedTasksCount.text =
                getString(R.string.you_have_uncompleted_tasks, activeNotesCount.toString())
        }
        binding.uncompletedTasksCard.setOnClickListener {

        }
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

    private fun setupCharts() {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 5f)) // الأسبوع 1
        entries.add(BarEntry(2f, 8f)) // الأسبوع 2
        entries.add(BarEntry(3f, 3f)) // الأسبوع 3
        entries.add(BarEntry(4f, 6f)) // الأسبوع 4

        val dataSet = BarDataSet(entries, "ساعات العمل الإضافي")

        dataSet.color = ContextCompat.getColor(requireContext(), R.color.light_blue)
        dataSet.valueTextColor = R.color.text_primary
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        binding.barChartOvertime.data = barData
        binding.barChartOvertime.description.isEnabled = true // إظهار الوصف
        binding.barChartOvertime.description.textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        binding.barChartOvertime.invalidate() // تحديث الرسم البياني

    }

    private fun setupPieChart() {
        val pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(40f, "الحضور"))
        pieEntries.add(PieEntry(30f, "الغياب"))
        pieEntries.add(PieEntry(20f, "العطلات "))
        pieEntries.add(PieEntry(10f, "الاجازات"))

        val pieDataSet = PieDataSet(pieEntries, "توزيع الايام")
        pieDataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.red),
            ContextCompat.getColor(requireContext(), R.color.blue),
            ContextCompat.getColor(requireContext(), R.color.yellow)
        )
        pieDataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        pieDataSet.valueTextSize = 12f


        val pieData = PieData(pieDataSet)
        binding.pieChartTasks.data = pieData
        binding.pieChartTasks.description.isEnabled = true // إظهار الوصف
        binding.pieChartTasks.description.textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        // تغيير لون نص الشرح (Legend)
        binding.pieChartTasks.legend.textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        binding.pieChartTasks.invalidate() // تحديث الرسم البياني
    }
    override fun onDestroyView() {
        dataFetchJob?.cancel()
        dataFetchJob = null
        super.onDestroyView()
    }
}