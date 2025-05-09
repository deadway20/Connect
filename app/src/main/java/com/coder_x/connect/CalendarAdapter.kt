package com.coder_x.connect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CalendarAdapter(
    private val weeks: List<List<String>>,
    private val onDayClick: (String) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.WeekViewHolder>() {

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
            holder.dayViews[index].text = day
            holder.dayViews[index].setOnClickListener { onDayClick(day) }
        }
    }

    override fun getItemCount(): Int = weeks.size
}