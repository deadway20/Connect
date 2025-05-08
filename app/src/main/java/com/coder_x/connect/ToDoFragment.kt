package com.coder_x.connect


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.coder_x.connect.databinding.FragmentToDoBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ToDoFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentToDoBinding
    private lateinit var addFab: FloatingActionButton
    private lateinit var imageFab: FloatingActionButton
    private var isOpen = false
    private lateinit var rotateOpenAnim: Animation
    private lateinit var rotateCloseAnim: Animation
    private lateinit var fromBottomAnim: Animation
    private lateinit var toBottomAnim: Animation


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentToDoBinding.inflate(inflater, container, false)

        rotateOpenAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_open_anim)
        rotateCloseAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_close_anim)
        fromBottomAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim)
        toBottomAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim)

        binding.mainFab.setOnClickListener(this)
        binding.addFab.setOnClickListener(this)
        binding.imageFab.setOnClickListener(this)

        // تأكد من إخفاء الأزرار الثانوية في البداية
        addFab.visibility = View.INVISIBLE
        imageFab.visibility = View.INVISIBLE



        return binding.root


    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_fab -> {
                animateFab()
            }

            R.id.add_fab -> {
                // قم بتنفيذ الإجراء الخاص بزر الإضافة هنا
            }

            R.id.image_fab -> {
                // قم بتنفيذ الإجراء الخاص بزر الصورة هنا
            }
        }
    }

    private fun animateFab() {
        if (isOpen) {
            binding.mainFab.startAnimation(rotateCloseAnim)
            addFab.startAnimation(toBottomAnim)
            imageFab.startAnimation(toBottomAnim)
            addFab.visibility = View.INVISIBLE
            imageFab.visibility = View.INVISIBLE
            addFab.isClickable = false
            imageFab.isClickable = false
            isOpen = false
        } else {
            binding.mainFab.startAnimation(rotateOpenAnim)
            addFab.startAnimation(fromBottomAnim)
            imageFab.startAnimation(fromBottomAnim)
            addFab.visibility = View.VISIBLE
            imageFab.visibility = View.VISIBLE
            addFab.isClickable = true
            imageFab.isClickable = true
            isOpen = true
        }
    }
}