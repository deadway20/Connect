package com.coder_x.connect

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.coder_x.connect.database.NoteEntity
import com.coder_x.connect.database.NoteViewModel
import com.coder_x.connect.databinding.FragmentToDoBinding
import com.coder_x.connect.SocialFragment.Companion.fontCustomize
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
        binding = FragmentToDoBinding.inflate(inflater, container, false)
        binding.notesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.notesRecycler.clipChildren = false
        binding.notesRecycler.clipToPadding = false
        viewBinding()
        noteViewModel = ViewModelProvider(this)[NoteViewModel::class.java]
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
            noteViewModel.delete(NoteEntity(
                id = item.id,
                title = item.todoTitle,
                timestamp = System.currentTimeMillis(),
                isCompleted = item.isCompleted,
                audioPath = item.audioPath,
                audioDuration = item.totalDuration,
                audioProgress = item.progress,
                isAudio = item.type == TodoType.VOICE,
                type = item.type.name
            ))

            // حذف العنصر من الـ Adapter فوراً
            adapter.removeItem(position)

            // إغلاق السويب
            swipeHelper.closeOpenedItem()

            Toast.makeText(requireContext(), "تم حذف المهمة", Toast.LENGTH_SHORT).show()
        }

        // إنشاء SwipeHelper وحفظ مرجع إليه
        swipeHelper = SwipeHelper(adapter)
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(binding.notesRecycler)

        // مراقبة التغييرات في قاعدة البيانات
        noteViewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            val newTodoList = notes.map { note ->
                TodoData(
                    id = note.id,
                    todoTitle = note.title,
                    todoTime = getTimeAgo(note.timestamp),
                    isCompleted = note.isCompleted,
                    audioPath = note.audioPath,
                    totalDuration = note.audioDuration,
                    progress = note.audioProgress,
                    type = TodoType.valueOf(note.type)
                )
            }

            // تحديث القائمة باستخدام الدالة الجديدة
            adapter.updateList(newTodoList)
        }

        CalendarHelper(
            requireContext(),
            binding.calendarContainer,
            object : CalendarHelper.CalendarInteractionListener {
                override fun onDayClicked(day: String, month: Int, year: Int) {
                    val date = "$day/${month + 1}/$year"
                    Toast.makeText(requireContext(), "Selected: $date", Toast.LENGTH_SHORT).show()
                }
            }
        ).setupCalendar()

        return binding.root
    }

    // باقي الدوال تبقى كما هي...
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
        setViews()
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
        isOpen = if (isOpen) {
            closeFabMenu()
            false
        } else {
            openFabMenu()
            true
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
    }

    override fun onDayClicked(day: String, month: Int, year: Int) {
        Toast.makeText(requireContext(), "Selected day: $day", Toast.LENGTH_SHORT).show()
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
                type = TodoType.TEXT.name
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
                type = TodoType.VOICE.name
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
                        type = TodoType.TEXT.name
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
                    type = TodoType.VOICE.name
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

    private fun setViews() {
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
    }
}