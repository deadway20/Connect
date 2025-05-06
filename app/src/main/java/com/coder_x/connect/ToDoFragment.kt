package com.coder_x.connect

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.CheckBox
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coder_x.connect.databinding.FragmentToDoBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ToDoFragment : Fragment() {
    private lateinit var binding: FragmentToDoBinding
    private lateinit var calendarLayout: ConstraintLayout
    private lateinit var dateScrollView: RecyclerView
    private lateinit var todoRecyclerView: RecyclerView
    private lateinit var addTaskButton: FloatingActionButton

    // Calendar state
    private var isCalendarExpanded = true
    private var selectedDate = Calendar.getInstance()

    // Sample todo tasks
    private val TaskItems = mutableListOf(
        TaskItem("change edittext outbox color", "04-23", false),
        TaskItem("fix font size in all topbar", "04-23", false),
        TaskItem("fix veiw direction in english language", "04-23", false),
        TaskItem("set profile bottombar ico to edit", "04-23", false),
        TaskItem("make graph in calendar Fragment", "04-23", false)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentToDoBinding.inflate(inflater, container, false)


        // Initialize views
        calendarLayout = binding.calendarLayout
        dateScrollView = binding.dateScrollView
        todoRecyclerView = binding.todoRecyclerView
        addTaskButton = binding.fabAddTask

        setupCalendar()
        setupDateScroll()
        setupTodoList()
        setupAddButton()

        return binding.root


    }


    private fun setupCalendar() {
        // Setup calendar expand/collapse behavior
        calendarLayout.setOnClickListener {
            toggleCalendarExpansion()
        }

        // Update the calendar header with current month and year
        updateCalendarHeader()
    }

    private fun updateCalendarHeader() {
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        view?.findViewById<TextView>(R.id.monthYearText)?.text =
            monthYearFormat.format(selectedDate.time)
    }

    private fun toggleCalendarExpansion() {
        val calendarGrid = view?.findViewById<View>(R.id.calendarGrid)
        val currentHeight = calendarGrid?.height ?: 0

        // Target height when collapsed (just the header row)
        val headerHeight = resources.getDimensionPixelSize(R.dimen.calendar_header_height)

        // Target height when expanded (full calendar)
        val expandedHeight = resources.getDimensionPixelSize(R.dimen.calendar_expanded_height)

        val startHeight = if (isCalendarExpanded) expandedHeight else headerHeight
        val endHeight = if (isCalendarExpanded) headerHeight else expandedHeight

        val anim = ValueAnimator.ofInt(startHeight, endHeight)
        anim.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            val layoutParams = calendarGrid?.layoutParams
            layoutParams?.height = value
            calendarGrid?.layoutParams = layoutParams
        }

        anim.interpolator = AccelerateDecelerateInterpolator()
        anim.duration = 300
        anim.start()

        isCalendarExpanded = !isCalendarExpanded
    }

    private fun setupDateScroll() {
        // Create a horizontal date picker for scrolling through days
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        dateScrollView.layoutManager = layoutManager

        // Generate dates for the current month
        val dates = generateDatesForCurrentMonth()

        // Create and set adapter
        val adapter = DateAdapter(dates) { date ->
            // Handle date selection
            selectedDate.time = date
            updateTodoList()
        }

        dateScrollView.adapter = adapter

        // Scroll to current day
        val today = Calendar.getInstance()
        val position = dates.indexOfFirst {
            val cal = Calendar.getInstance()
            cal.time = it
            cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
        }
        if (position != -1) {
            dateScrollView.scrollToPosition(position)
        }
    }

    private fun generateDatesForCurrentMonth(): List<Date> {
        val dates = mutableListOf<Date>()
        val cal = Calendar.getInstance()

        // Set to first day of month
        cal.set(Calendar.DAY_OF_MONTH, 1)

        // Add the last few days of previous month
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        cal.add(Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1))

        // Generate 42 days (6 weeks) to ensure we have enough days
        repeat(42) {
            dates.add(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        return dates
    }

    private fun setupTodoList() {
        val layoutManager = LinearLayoutManager(context)
        todoRecyclerView.layoutManager = layoutManager

        val adapter = TodoAdapter(TaskItems) { position, isChecked ->
            // Handle task completion
            TaskItems[position].isCompleted = isChecked
        }

        todoRecyclerView.adapter = adapter
    }

    private fun updateTodoList() {
        // In a real app, you would filter tasks based on the selected date
        todoRecyclerView.adapter?.notifyDataSetChanged()
    }

    private fun setupAddButton() {
        addTaskButton.setOnClickListener {
            // Show dialog to add new task
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        // Implementation for showing a dialog to add a new task
        // This would include EditText for task name and potentially other fields
        // For now, we'll just add a placeholder task
        val dateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
        val newTask = TaskItem("New Task", dateFormat.format(selectedDate.time), false)
        TaskItems.add(newTask)
        todoRecyclerView.adapter?.notifyItemInserted(TaskItems.size - 1)
    }


    inner class TodoAdapter(
        private val tasks: List<TaskItem>,
        private val onTaskCheckedListener: (Int, Boolean) -> Unit,
    ) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_todo_task, parent, false)
            return TodoViewHolder(view)
        }

        override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
            val task = tasks[position]
            holder.bind(task, position)
        }

        override fun getItemCount(): Int = tasks.size

        inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckbox)
            private val titleText: TextView = itemView.findViewById(R.id.taskTitle)
            private val dateText: TextView = itemView.findViewById(R.id.taskDate)

            fun bind(task: TaskItem, position: Int) {
                checkBox.isChecked = task.isCompleted
                titleText.text = task.title
                dateText.text = task.date

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    onTaskCheckedListener(position, isChecked)
                }
            }
        }
    }

    inner class DateAdapter(
        private val dates: List<Date>,
        private val onDateSelectedListener: (Date) -> Unit,
    ) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

        private val today = Calendar.getInstance()
        private var selectedPosition = -1

        init {
            // Find position of today's date and select it
            selectedPosition = dates.indexOfFirst {
                val cal = Calendar.getInstance()
                cal.time = it
                cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) && cal.get(
                    Calendar.MONTH
                ) == today.get(Calendar.MONTH)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_date, parent, false)
            return DateViewHolder(view)
        }

        override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
            val date = dates[position]
            holder.bind(date, position == selectedPosition)
        }

        override fun getItemCount(): Int = dates.size

        inner class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val dayText: TextView = itemView.findViewById(R.id.dayText)
            private val dayOfWeekText: TextView = itemView.findViewById(R.id.dayOfWeekText)
            private val dateIndicator: View = itemView.findViewById(R.id.dateIndicator)

            fun bind(date: Date, isSelected: Boolean) {
                val cal = Calendar.getInstance()
                cal.time = date

                val dayFormat = SimpleDateFormat("d", Locale.getDefault())
                val dayOfWeekFormat = SimpleDateFormat("EEE", Locale.getDefault())

                dayText.text = dayFormat.format(date)
                dayOfWeekText.text = dayOfWeekFormat.format(date)

                // Highlight current day
                val isToday =
                    cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH) && cal.get(
                        Calendar.MONTH
                    ) == today.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)

                // Apply styling based on selection and if it's today
                if (isToday) {
                    dateIndicator.visibility = View.VISIBLE
                    dateIndicator.setBackgroundResource(R.drawable.today_indicator)
                } else if (isSelected) {
                    dateIndicator.visibility = View.VISIBLE
                    dateIndicator.setBackgroundResource(R.drawable.selected_date_indicator)
                } else {
                    dateIndicator.visibility = View.INVISIBLE
                }

                // Set click listener
                itemView.setOnClickListener {
                    val oldSelected = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(oldSelected)
                    notifyItemChanged(selectedPosition)
                    onDateSelectedListener(date)
                }
            }
        }
    }
}