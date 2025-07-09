package com.coder_x.connect.profile

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.coder_x.connect.R
import androidx.recyclerview.widget.RecyclerView
import com.coder_x.connect.database.NoteEntity
import com.coder_x.connect.database.NoteViewModel
import com.coder_x.connect.database.TaskFilter
import com.coder_x.connect.databinding.FragmentEditServerBinding
import com.coder_x.connect.helpers.CalendarHelper
import com.coder_x.connect.helpers.SharedPrefsHelper
import com.coder_x.connect.helpers.SwipeHelper
import com.coder_x.connect.main.MainFragment
import com.coder_x.connect.social.SocialFragment.Companion.fontCustomize
import com.coder_x.connect.todo.TextTodoBottomSheet
import com.coder_x.connect.todo.TodoAdapter
import com.coder_x.connect.todo.TodoData
import com.coder_x.connect.todo.TodoType
import com.coder_x.connect.todo.VoiceTodoBottomSheet
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditServerFragment : Fragment(){
    private lateinit var binding: FragmentEditServerBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEditServerBinding.inflate(inflater, container, false)
        return binding.root
    }

}