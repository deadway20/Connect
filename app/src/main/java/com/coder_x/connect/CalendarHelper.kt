package com.coder_x.connect
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarHelper(
    private val context: Context,
    private val rootView: ViewGroup,
    private val listener: CalendarInteractionListener
) {
    interface CalendarInteractionListener {
        fun onDayClicked(day: String, month: Int, year: Int)
    }

    // العناصر الأساسية
    private lateinit var monthViewPager: ViewPager2
    private lateinit var weekView: ViewGroup
    private lateinit var toggleButton: ImageButton
    private lateinit var monthYearText: TextView
    private lateinit var btnPrevious: ImageButton
    private lateinit var btnNext: ImageButton

    // حالة التقويم
    private var isExpanded = false
    private var currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH)
    private var currentYear: Int = Calendar.getInstance().get(Calendar.YEAR)
    private var selectedDay: String = Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()

    fun setupCalendar() {
        val view = LayoutInflater.from(context).inflate(R.layout.calendar_layout, rootView, false)
        rootView.addView(view)

        initViews(view)
        setupWeekHeader()
        setupMonthPager()
        setupCurrentWeekView()
        setupButtons()
        updateCalendarState()

    }

    private fun initViews(view: View) {
        monthViewPager = view.findViewById(R.id.monthViewPager)
        weekView = view.findViewById(R.id.weekView)
        toggleButton = view.findViewById(R.id.btnToggle)
        monthYearText = view.findViewById(R.id.txtMonthYear)
        btnPrevious = view.findViewById(R.id.btnPrevious)
        btnNext = view.findViewById(R.id.btnNext)
    }

    private fun setupMonthPager() {
        monthViewPager.adapter = MonthPagerAdapter(context, object : MonthPagerAdapter.CalendarInteractionListener {
            override fun onDayClicked(day: String) {
                selectedDay = day
                listener.onDayClicked(day, currentMonth, currentYear)
                updateCurrentWeekSelection()
            }
        })

        monthViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateCurrentMonth(position)
                setupCurrentWeekView()
            }
        })

        monthViewPager.setCurrentItem(Int.MAX_VALUE / 2, false)
        monthViewPager.offscreenPageLimit = 2
    }

    private fun updateCurrentMonth(position: Int) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, position - (Int.MAX_VALUE / 2))
        currentMonth = calendar.get(Calendar.MONTH)
        currentYear = calendar.get(Calendar.YEAR)
        updateMonthYearDisplay()
    }

    private fun setupCurrentWeekView() {
        val currentWeek = getWeekForDay(selectedDay.toIntOrNull() ?: 1)

        currentWeek.forEachIndexed { index, day ->
            val dayView = weekView.findViewById<TextView>(
                context.resources.getIdentifier("currentDay${index+1}", "id", context.packageName)
            )

            dayView.text = day
            dayView.setOnClickListener {
                selectedDay = day
                listener.onDayClicked(day, currentMonth, currentYear)
                updateCurrentWeekSelection()
            }

            updateDayAppearance(dayView, day)
        }
    }

    private fun getWeekForDay(day: Int): List<String> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, day)
        }

        val week = mutableListOf<String>()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        repeat(7) {
            week.add(calendar.get(Calendar.DAY_OF_MONTH).toString())
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return week
    }

    private fun updateDayAppearance(dayView: TextView, day: String) {
        if (day == selectedDay) {
            dayView.setBackgroundResource(R.drawable.selected_day_background)
            dayView.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            dayView.background = null
            dayView.setTextColor(ContextCompat.getColor(context, R.color.black))
        }
    }

    private fun updateCurrentWeekSelection() {
        val currentWeek = getWeekForDay(selectedDay.toIntOrNull() ?: 1)

        currentWeek.forEachIndexed { index, day ->
            val dayView = weekView.findViewById<TextView>(
                context.resources.getIdentifier("currentDay${index+1}", "id", context.packageName)
            )
            updateDayAppearance(dayView, day)
        }
    }

    private fun setupButtons() {
        toggleButton.setOnClickListener { toggleCalendar() }
        btnPrevious.setOnClickListener { monthViewPager.currentItem-- }
        btnNext.setOnClickListener { monthViewPager.currentItem++ }
    }

    private fun toggleCalendar() {
        isExpanded = !isExpanded
        updateCalendarState()
    }

    private fun updateMonthYearDisplay() {
        val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(
            Calendar.getInstance().apply {
                set(Calendar.MONTH, currentMonth)
                set(Calendar.YEAR, currentYear)
            }.time
        )
        monthYearText.text = monthName
    }


    private fun setupWeekHeader() {
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        daysOfWeek.forEachIndexed { index, day ->
            val dayView = rootView.findViewById<TextView>(
                context.resources.getIdentifier("day${index+1}", "id", context.packageName)
            )
            dayView?.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            dayView?.text = day
        }
    }

    private fun updateCalendarState() {
        if (isExpanded) {
            monthViewPager.visibility = View.VISIBLE
            weekView.visibility = View.GONE
        } else {
            monthViewPager.visibility = View.GONE
            weekView.visibility = View.VISIBLE
        }
    }
}