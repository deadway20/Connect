package com.coder_x.connect.CollapsibleCalendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.coder_x.connect.R

class CalendarAdapter(
    private val weeks: List<List<String>>,
    private var selectedDay: String = ""
) : RecyclerView.Adapter<CalendarAdapter.WeekViewHolder>() {

    private var dayClickListener: ((String) -> Unit)? = null

    fun setOnDayClickListener(listener: (String) -> Unit) {
        dayClickListener = listener
    }

    fun updateSelectedDay(day: String) {
        val previousSelected = selectedDay
        selectedDay = day
        notifyItemChanged(findPositionOfDay(previousSelected))
        notifyItemChanged(findPositionOfDay(day))
    }

    inner class WeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayViews: List<TextView> = listOf(
            itemView.findViewById(R.id.day1),
            itemView.findViewById(R.id.day2),
            itemView.findViewById(R.id.day3),
            itemView.findViewById(R.id.day4),
            itemView.findViewById(R.id.day5),
            itemView.findViewById(R.id.day6),
            itemView.findViewById(R.id.day7)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_week, parent, false)
        return WeekViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        val week = weeks[position]
        week.forEachIndexed { index, day ->
            val dayView = holder.dayViews[index]
            dayView.text = day

            if (day.isEmpty()) {
                dayView.visibility = View.INVISIBLE
                return@forEachIndexed
            }

            dayView.visibility = View.VISIBLE
            updateDayAppearance(dayView, day)

            dayView.setOnClickListener {
                if (day.isNotEmpty()) {
                    selectedDay = day
                    dayClickListener?.invoke(day)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun findPositionOfDay(day: String): Int {
        weeks.forEachIndexed { index, week ->
            if (week.contains(day)) return index
        }
        return -1
    }

    private fun updateDayAppearance(dayView: TextView, day: String) {
        if (day == selectedDay) {
            dayView.setBackgroundResource(R.drawable.selected_day_background)
            dayView.setTextColor(ContextCompat.getColor(dayView.context, R.color.text_primary))
        } else {
            dayView.setBackgroundResource(R.color.card_background)
            dayView.setTextColor(ContextCompat.getColor(dayView.context, R.color.text_primary))
        }
    }

    override fun getItemCount(): Int = weeks.size
}