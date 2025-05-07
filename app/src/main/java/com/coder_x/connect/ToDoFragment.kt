package com.coder_x.connect


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.coder_x.connect.databinding.FragmentToDoBinding


class ToDoFragment : Fragment() {
    private lateinit var binding: FragmentToDoBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentToDoBinding.inflate(inflater, container, false)




        return binding.root


    }
}