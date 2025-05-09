package com.coder_x.connect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarHelper(
    private val context: Context,
    private val rootView: ViewGroup,
    private val listener: CalendarInteractionListener
) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var toggleButton: ImageView
    private lateinit var monthYearText: TextView
    private var isExpanded = true // تغيير القيمة الافتراضية لـ true

    interface CalendarInteractionListener {
        fun onDayClicked(day: String)
    }

    fun setupCalendar(month: Int, year: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.calendar_layout, rootView, false)
        rootView.addView(view)

        // تهيئة العناصر
        recyclerView = view.findViewById(R.id.calendarRecycler)
        toggleButton = view.findViewById(R.id.btnToggle)
        monthYearText = view.findViewById(R.id.txtMonthYear)
        val weekHeader = view.findViewById<View>(R.id.weekHeader)

        // تعيين شهر وسنة
        val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(Calendar.getInstance().apply {
            set(Calendar.MONTH, month)
            set(Calendar.YEAR, year)
        }.time)
        monthYearText.text = "$monthName $year"

        // إعداد أيام الأسبوع في الهيدر
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        daysOfWeek.forEachIndexed { index, day ->
            view.findViewById<TextView>(context.resources.getIdentifier("day${index+1}", "id", context.packageName)).text = day
        }

        // إعداد الـ RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = CalendarAdapter(getCalendarData(), listener::onDayClicked)

        // إظهار الأسبوع الأول افتراضيًا
        recyclerView.visibility = View.VISIBLE
        toggleButton.setImageResource(R.drawable.ic_arrow_up)

        // إعداد النقر على زر التبديل
        toggleButton.setOnClickListener {
            toggleCalendar()
        }

        weekHeader.setOnClickListener {
            toggleCalendar()
        }
    }

    private fun toggleCalendar() {
        isExpanded = !isExpanded
        recyclerView.visibility = if (isExpanded) View.VISIBLE else View.GONE
        toggleButton.setImageResource(
            if (isExpanded) R.drawable.ic_arrow_up
            else R.drawable.ic_arrow_down
        )
    }

    private fun getCalendarData(): List<List<String>> = listOf(
        listOf("27", "28", "29", "30", "1", "2", "3"),
        listOf("4", "5", "6", "7", "8", "9", "10"),
        listOf("11", "12", "13", "14", "15", "16", "17"),
        listOf("18", "19", "20", "21", "22", "23", "24"),
        listOf("25", "26", "27", "28", "29", "30", "31")
    )
}