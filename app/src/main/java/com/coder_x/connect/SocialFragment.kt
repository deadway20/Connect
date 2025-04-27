package com.coder_x.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.coder_x.connect.database.PostEntity
import com.coder_x.connect.databinding.FragmentSocialBinding

class SocialFragment : Fragment() {
    private lateinit var binding: FragmentSocialBinding
    private lateinit var adapter: PostAdapter
    private lateinit var fragmentManager: FragmentManager

    private val dataList = listOf<PostEntity>()
    private lateinit var addPostDialog: AddPostFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentSocialBinding.inflate(inflater, container, false)
        adapter = PostAdapter(emptyList())
        fragmentManager = requireActivity().supportFragmentManager
        addPostDialog = AddPostFragment()
        binding.posrRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.posrRecyclerView.adapter = adapter


        binding.tvWhatOnMind.setOnClickListener {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, addPostDialog)
                .addToBackStack(null).commit()
        }
        binding.fabAddPost.setOnClickListener {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, addPostDialog)
                .addToBackStack(null).commit()
        }

        // make code on press back button go to another fragment
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (addPostDialog.isVisible) {
                    fragmentManager.popBackStack()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)






        return binding.root


    }
}