package com.coder_x.connect.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.coder_x.connect.databinding.FragmentCalendarBinding
import com.coder_x.connect.helpers.SharedPrefsHelper

class CalendarFragment : Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var prefsHelper: SharedPrefsHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        prefsHelper = SharedPrefsHelper(requireContext())


        return binding.root
    }


}