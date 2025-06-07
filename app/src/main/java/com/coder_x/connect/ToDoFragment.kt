package com.coder_x.connect

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.coder_x.connect.database.NoteEntity
import com.coder_x.connect.database.NoteViewModel
import com.coder_x.connect.databinding.FragmentToDoBinding
import com.coder_x.connect.SocialFragment.Companion.fontCustomize
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ToDoFragment : Fragment(), View.OnClickListener, CalendarHelper.CalendarInteractionListener {
    private lateinit var binding: FragmentToDoBinding
    private lateinit var addFab: FloatingActionButton
    private lateinit var textTodoBtn: FloatingActionButton
    private lateinit var voiceTodoBtn: FloatingActionButton
    private lateinit var noteViewModel: NoteViewModel
    private lateinit var adapter: TodoAdapter
    private lateinit var prefsHelper: SharedPrefsHelper
    private var currentSelectedDate: String = LocalDate.now().toString()
    private lateinit var swipeHelper: SwipeHelper // إضافة مرجع للـ SwipeHelper
    private val todoList = mutableListOf<TodoData>()
    private var isOpen = false
    private val animationDuration = 300L
    private val fabOffset = 150f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java] // Initialize noteViewModel first
        binding = FragmentToDoBinding.inflate(inflater, container, false)
        binding.notesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.notesRecycler.clipChildren = false
        binding.notesRecycler.clipToPadding = false
        viewBinding() // Call viewBinding after noteViewModel is initialized
        currentSelectedDate = noteViewModel.selectedDate.value ?: LocalDate.now().toString()
        adapter = TodoAdapter(todoList, requireContext())
        binding.notesRecycler.adapter = adapter
        prefsHelper = SharedPrefsHelper(requireContext())

        adapter.onEdit = { position ->
            val item = adapter.getItemAt(position)
            when (item.type) {
                TodoType.TEXT -> editTextTodoBottomSheet(item)
                TodoType.VOICE -> editVoiceTodoBottomSheet(item)
            }
        }

        adapter.onDelete = { position ->
            val item = adapter.getItemAt(position)

            // حذف العنصر من قاعدة البيانات
            noteViewModel.delete(
                NoteEntity(
                    id = item.id,
                    title = item.todoTitle,
                    timestamp = System.currentTimeMillis(),
                    isCompleted = item.isCompleted,
                    audioPath = item.audioPath,
                    audioDuration = item.totalDuration,
                    audioProgress = item.progress,
                    isAudio = item.type == TodoType.VOICE,
                    type = item.type.name
                )
            )

            // Delete the item from the adapter
            adapter.removeItem(position)

            // close the opened item
            swipeHelper.closeOpenedItem()

        }

        // Initialize the SwipeHelper
        swipeHelper = SwipeHelper(adapter)
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(binding.notesRecycler)

        // Observer for the list of notes
        currentSelectedDate = LocalDate.now().toString()
        observeTasksByDate(currentSelectedDate)

        CalendarHelper(
            requireContext(),
            binding.calendarContainer,
            this // Pass the fragment as the listener
        ).setupCalendar()

        return binding.root
    }
    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_todo_btn -> animateFab()
            R.id.text_todo_btn -> {
                closeFabMenu()
                showTextTodoBottomSheet()
            }

            R.id.voice_todo_btn -> {
                closeFabMenu()
                showVoiceTodoBottomSheet()
            }
        }
    }
    private fun viewBinding() {
        prefsHelper = SharedPrefsHelper(requireContext())
        addFab = binding.addTodoBtn
        textTodoBtn = binding.textTodoBtn
        voiceTodoBtn = binding.voiceTodoBtn

        fontCustomize(requireContext(), binding.topBarTitle)
        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainFragment()).commit()
        }
        setGreetingMsg()
        binding.addTodoBtn.setOnClickListener(this)
        binding.textTodoBtn.setOnClickListener(this)
        binding.voiceTodoBtn.setOnClickListener(this)

        addFab.visibility = View.VISIBLE
        textTodoBtn.visibility = View.INVISIBLE
        voiceTodoBtn.visibility = View.INVISIBLE
        textTodoBtn.isClickable = false
        voiceTodoBtn.isClickable = false
    }

    private fun animateFab() {
        if (isOpen) {
            closeFabMenu()
        } else {
            openFabMenu()
        }
    }

    private fun openFabMenu() {
        val rotateOpen = ObjectAnimator.ofFloat(binding.addTodoBtn, View.ROTATION, 0f, 45f)
        rotateOpen.duration = animationDuration

        val addFabTranslationY =
            ObjectAnimator.ofFloat(textTodoBtn, View.TRANSLATION_Y, fabOffset, 0f)
        val addFabAlpha = ObjectAnimator.ofFloat(textTodoBtn, View.ALPHA, 0f, 1f)
        textTodoBtn.visibility = View.VISIBLE
        textTodoBtn.isClickable = true

        val imageFabTranslationY =
            ObjectAnimator.ofFloat(voiceTodoBtn, View.TRANSLATION_Y, fabOffset * 2, 0f)
        val imageFabAlpha = ObjectAnimator.ofFloat(voiceTodoBtn, View.ALPHA, 0f, 1f)
        voiceTodoBtn.visibility = View.VISIBLE
        voiceTodoBtn.isClickable = true

        val openAnimatorSet = AnimatorSet()
        openAnimatorSet.playTogether(
            rotateOpen, addFabTranslationY, addFabAlpha, imageFabTranslationY, imageFabAlpha
        )
        openAnimatorSet.duration = animationDuration
        openAnimatorSet.start()
        isOpen = true
    }

    private fun closeFabMenu() {
        val rotateClose = ObjectAnimator.ofFloat(binding.addTodoBtn, View.ROTATION, 45f, 0f)
        rotateClose.duration = animationDuration

        val addTodoFabTranslationY =
            ObjectAnimator.ofFloat(textTodoBtn, View.TRANSLATION_Y, 0f, fabOffset)
        val addFabAlpha = ObjectAnimator.ofFloat(textTodoBtn, View.ALPHA, 1f, 0f)

        val voiceFabTranslationY =
            ObjectAnimator.ofFloat(voiceTodoBtn, View.TRANSLATION_Y, 0f, fabOffset * 2)
        val imageFabAlpha = ObjectAnimator.ofFloat(voiceTodoBtn, View.ALPHA, 1f, 0f)

        val closeAnimatorSet = AnimatorSet()
        closeAnimatorSet.playTogether(
            rotateClose, addTodoFabTranslationY, addFabAlpha, voiceFabTranslationY, imageFabAlpha
        )
        closeAnimatorSet.duration = animationDuration
        closeAnimatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                textTodoBtn.visibility = View.INVISIBLE
                voiceTodoBtn.visibility = View.INVISIBLE
                textTodoBtn.isClickable = false
                voiceTodoBtn.isClickable = false
            }
        })
        closeAnimatorSet.start()
        isOpen = false
    }

    override fun onDayClicked(day: String, month: Int, year: Int) {
        // استخدام Locale.US لضمان تنسيق التاريخ بشكل ثابت بغض النظر عن لغة الجهاز
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day.toInt())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val selectedDate = dateFormat.format(calendar.time)

        currentSelectedDate = selectedDate
        noteViewModel.setSelectedDate(selectedDate) // تحديث التاريخ في ViewModel
        observeTasksByDate(selectedDate) // إعادة تحميل البيانات والعدد للتاريخ الجديد
        Toast.makeText(requireContext(), "Selected: $selectedDate", Toast.LENGTH_SHORT).show()
    }

    private fun showTextTodoBottomSheet() {
        val sheet = TextTodoBottomSheet()
        sheet.onTodoAdded = { title ->
            val note = NoteEntity(
                title = title,
                timestamp = System.currentTimeMillis(),
                isCompleted = false,
                audioPath = null,
                audioDuration = null,
                isAudio = false,
                type = TodoType.TEXT.name,
                selectedDate = currentSelectedDate // إضافة التاريخ الحالي للمهمة
            )
            noteViewModel.insert(note)
        }
        sheet.show(parentFragmentManager, "TextTodoSheet")
    }

    private fun showVoiceTodoBottomSheet() {
        val sheet = VoiceTodoBottomSheet()
        sheet.onVoiceTodoAdded = { title, path, duration ->
            val note = NoteEntity(
                title = title,
                isCompleted = false,
                timestamp = System.currentTimeMillis(),
                audioPath = path,
                audioDuration = duration.toLongOrNull(),
                audioProgress = 0,
                isAudio = true,
                type = TodoType.VOICE.name,
                selectedDate = currentSelectedDate // إضافة التاريخ الحالي للمهمة
            )
            noteViewModel.insert(note)
        }
        sheet.show(parentFragmentManager, "VoiceTodoSheet")
    }

    private fun editTextTodoBottomSheet(item: TodoData) {
        val sheet = TextTodoBottomSheet().apply {
            setTodoForEditing(item.id.toString(), item.todoTitle)
            onTodoUpdated = { todoId, title ->
                noteViewModel.update(
                    NoteEntity(
                        id = item.id,
                        title = title,
                        timestamp = System.currentTimeMillis(),
                        isCompleted = item.isCompleted,
                        audioPath = null,
                        audioDuration = null,
                        isAudio = false,
                        type = TodoType.TEXT.name,
                        selectedDate = item.selectedDate ?: currentSelectedDate // استخدام تاريخ المهمة أو التاريخ الحالي
                    )
                )
                Toast.makeText(requireContext(), "تم تحديث المهمة النصية", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        sheet.show(parentFragmentManager, "EditTextTodoSheet")
    }

    private fun editVoiceTodoBottomSheet(item: TodoData) {
        val sheet = VoiceTodoBottomSheet().apply {
            setTodoForEditing(
                item.id, NoteEntity(
                    id = item.id,
                    title = item.todoTitle,
                    timestamp = System.currentTimeMillis(),
                    isCompleted = item.isCompleted,
                    audioPath = item.audioPath,
                    audioDuration = item.totalDuration,
                    audioProgress = item.progress,
                    isAudio = true,
                    type = TodoType.VOICE.name,
                    selectedDate = item.selectedDate ?: currentSelectedDate // استخدام تاريخ المهمة أو التاريخ الحالي
                )
            )
            onVoiceTodoUpdated = { updatedTodo ->
                noteViewModel.update(updatedTodo)
                Toast.makeText(requireContext(), "تم تحديث المهمة الصوتية", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        sheet.show(parentFragmentManager, "EditVoiceTodoSheet")
    }

    private fun getTimeAgo(time: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            seconds < 60 -> "منذ ثواني"
            minutes < 60 -> "منذ ${minutes} دقيقة"
            hours < 24 -> "منذ ${hours} ساعة"
            days == 1L -> "منذ يوم"
            days < 7 -> "منذ ${days} أيام"
            else -> {
                val sdf = java.text.SimpleDateFormat("dd MMM", Locale("ar"))
                sdf.format(Date(time))
            }
        }
    }

    private fun setGreetingMsg() {
        val empName = prefsHelper.getEmployeeName()
        val empImg = prefsHelper.getEmployeeImageBitmap()
        val currentTime = LocalTime.now()
        val morning = LocalTime.of(6, 0)
        val evening = LocalTime.of(12, 0)
        val night = LocalTime.of(18, 0)
        val nightGreeting = resources.getString(R.string.night_greeting)
        val morningGreeting = resources.getString(R.string.morning_greeting)
        val eveningGreeting = resources.getString(R.string.evening_greeting)
        binding.userImage.setImageBitmap(empImg)
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

        val today = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        // تم نقل استدعاء getTasksCountByDate إلى observeTasksByDate لضمان تحديث العدد مع تغيير اليوم
    }

    private fun observeTasksByDate(dateString: String) {
        noteViewModel.setSelectedDate(dateString) // تأكد من تحديث التاريخ في ViewModel

        noteViewModel.tasksBySelectedDate.observe(viewLifecycleOwner) { notes ->
            val newTodoList = notes.mapNotNull { note ->
                if (note.type.isNotEmpty()) {
                    TodoData(
                        id = note.id,
                        todoTitle = note.title,
                        todoTime = getTimeAgo(note.timestamp),
                        isCompleted = note.isCompleted,
                        audioPath = note.audioPath,
                        totalDuration = note.audioDuration,
                        progress = note.audioProgress,
                        type = TodoType.valueOf(note.type),
                        selectedDate = note.selectedDate
                    )
                } else null
            }
            adapter.updateList(newTodoList)
        }

        // عد المهام عند تغيير التاريخ
        noteViewModel.getTasksCountByDate(dateString).observe(viewLifecycleOwner) { count ->
            binding.tasksCount.text = getString(R.string.tasks_count, count.toString())
        }
    }
}