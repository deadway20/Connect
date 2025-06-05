package com.coder_x.connect
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class MonthPagerAdapter(
    private val context: Context,
    private val listener: CalendarInteractionListener
) : RecyclerView.Adapter<MonthPagerAdapter.MonthViewHolder>() {

    interface CalendarInteractionListener {
        fun onDayClicked(day: String)
    }

    private val calendar = Calendar.getInstance()
    private var currentPosition = Int.MAX_VALUE / 2
    private var currentAdapters = mutableMapOf<Int, CalendarAdapter>()

    inner class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.calendarRecycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_month_calendar, parent, false)

        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val offset = position - currentPosition
        val tempCalendar = calendar.clone() as Calendar
        tempCalendar.add(Calendar.MONTH, offset)

        val month = tempCalendar.get(Calendar.MONTH)
        val year = tempCalendar.get(Calendar.YEAR)

        val weeks = getCalendarData(month, year)
        val adapter = CalendarAdapter(weeks).apply {
            setOnDayClickListener { day ->
                if (day.isNotEmpty()) {
                    listener.onDayClicked(day)
                }
            }
        }

        currentAdapters[position] = adapter
        holder.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
    }

    fun updateSelectedDay(day: String) {
        currentAdapters.forEach { (_, adapter) ->
            adapter.updateSelectedDay(day)
        }
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    private fun getCalendarData(month: Int, year: Int): List<List<String>> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // Sunday = 0

        val weeks = mutableListOf<List<String>>()
        val currentWeek = mutableListOf<String>()

        // إضافة أيام فارغة لليوم الأول من الشهر
        repeat(firstDayOfWeek) {
            currentWeek.add("")
        }

        for (day in 1..maxDay) {
            currentWeek.add(day.toString())
            if (currentWeek.size == 7) {
                weeks.add(currentWeek.toList())
                currentWeek.clear()
            }
        }

        // إضافة الأسبوع الأخير إذا لم يكن مكتملاً
        if (currentWeek.isNotEmpty()) {
            while (currentWeek.size < 7) {
                currentWeek.add("")
            }
            weeks.add(currentWeek)
        }

        return weeks
    }
}