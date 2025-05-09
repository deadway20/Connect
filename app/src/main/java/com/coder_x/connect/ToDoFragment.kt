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
import com.coder_x.connect.databinding.FragmentToDoBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class ToDoFragment : Fragment(), View.OnClickListener , CalendarHelper.CalendarInteractionListener{
    private lateinit var binding: FragmentToDoBinding
    private lateinit var addFab: FloatingActionButton
    private lateinit var imageFab: FloatingActionButton
    private var isOpen = false
    private val animationDuration = 300L // Duration for animations in milliseconds
    private val fabOffset = 150f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentToDoBinding.inflate(inflater, container, false)
        viewBinding()


        // الحصول على الشهر والسنة الحاليين
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        CalendarHelper(requireContext(), binding.calendarContainer, this).setupCalendar(month, year)
        return binding.root
    }



    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_fab -> {
                animateFab()
            }

            R.id.add_fab -> {
                // Handle add_fab click
            }

            R.id.image_fab -> {
                // Handle image_fab click
            }
        }
    }
    private fun viewBinding() {
        addFab = binding.addFab
        imageFab = binding.imageFab
        binding.mainFab.setOnClickListener(this)
        binding.addFab.setOnClickListener(this)
        binding.imageFab.setOnClickListener(this)
        // Ensure sub-Fabs are initially hidden
        addFab.visibility = View.INVISIBLE
        imageFab.visibility = View.INVISIBLE
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
        // Animate main FAB rotation
        val rotateOpen = ObjectAnimator.ofFloat(binding.mainFab, View.ROTATION, 0f, 45f)
        rotateOpen.duration = animationDuration

        // Animate addFab appearance
        val addFabTranslationY = ObjectAnimator.ofFloat(addFab, View.TRANSLATION_Y, fabOffset, 0f)
        val addFabAlpha = ObjectAnimator.ofFloat(addFab, View.ALPHA, 0f, 1f)
        addFab.visibility = View.VISIBLE
        addFab.isClickable = true

        // Animate imageFab appearance
        val imageFabTranslationY =
            ObjectAnimator.ofFloat(imageFab, View.TRANSLATION_Y, fabOffset * 2, 0f)
        val imageFabAlpha = ObjectAnimator.ofFloat(imageFab, View.ALPHA, 0f, 1f)
        imageFab.visibility = View.VISIBLE
        imageFab.isClickable = true

        // Set up and play the animations together
        val openAnimatorSet = AnimatorSet()
        openAnimatorSet.playTogether(
            rotateOpen,
            addFabTranslationY,
            addFabAlpha,
            imageFabTranslationY,
            imageFabAlpha
        )
        openAnimatorSet.duration = animationDuration
        openAnimatorSet.start()
    }
    private fun closeFabMenu() {
        // Animate main FAB rotation
        val rotateClose = ObjectAnimator.ofFloat(binding.mainFab, View.ROTATION, 45f, 0f)
        rotateClose.duration = animationDuration

        // Animate addFab disappearance
        val addFabTranslationY = ObjectAnimator.ofFloat(addFab, View.TRANSLATION_Y, 0f, fabOffset)
        val addFabAlpha = ObjectAnimator.ofFloat(addFab, View.ALPHA, 1f, 0f)

        // Animate imageFab disappearance
        val imageFabTranslationY =
            ObjectAnimator.ofFloat(imageFab, View.TRANSLATION_Y, 0f, fabOffset * 2)
        val imageFabAlpha = ObjectAnimator.ofFloat(imageFab, View.ALPHA, 1f, 0f)

        // Set up and play the animations together
        val closeAnimatorSet = AnimatorSet()
        closeAnimatorSet.playTogether(
            rotateClose,
            addFabTranslationY,
            addFabAlpha,
            imageFabTranslationY,
            imageFabAlpha
        )
        closeAnimatorSet.duration = animationDuration
        closeAnimatorSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                addFab.visibility = View.INVISIBLE
                imageFab.visibility = View.INVISIBLE
                addFab.isClickable = false
                imageFab.isClickable = false
            }
        })
        closeAnimatorSet.start()
    }

    override fun onDayClicked(day: String) {
        // Handle the day click event
        Toast.makeText(requireContext(), "Selected day: $day", Toast.LENGTH_SHORT).show()

        // Or if you want to do something more advanced:
        /*
        val selectedDate = "$day ${monthYearText.text}" // Combine day with month/year
        viewModel.setSelectedDate(selectedDate)
        findNavController().navigate(R.id.action_to_details)
        */
    }
}