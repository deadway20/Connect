package com.coder_x.connect

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.coder_x.connect.database.PostViewModel
import com.coder_x.connect.databinding.FragmentSocialBinding

class SocialFragment : Fragment(), PostAdapter.OnSocialActionListener {
    private lateinit var binding: FragmentSocialBinding
    private lateinit var adapter: PostAdapter
    private lateinit var fragmentManager: FragmentManager

    private val postViewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSocialBinding.inflate(inflater, container, false)
        adapter = PostAdapter(emptyList(), this)
        fragmentManager = requireActivity().supportFragmentManager
        binding.socialRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.socialRecyclerView.adapter = adapter

        fontCustomize(requireContext(), binding.topBarTitle)
        binding.backBtn.setOnClickListener {
            fragmentManager.popBackStack()
        }

        postViewModel.allPosts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        binding.fabAddPost.setOnClickListener {
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddPostFragment())
                .addToBackStack(null)
                .commit()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (AddPostFragment().isVisible) {
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

    override fun onCreatePostClicked() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, AddPostFragment())
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun fontCustomize(context: Context, textView: TextView) {
            val resources = context.resources
            val fontResId =
                resources.getIdentifier("angelos", "font", context.packageName)
            textView.typeface = resources.getFont(fontResId)
            // make font color baby blue
            val babyBlue = Color.rgb(137, 207, 240) // Define baby blue color
            textView.setTextColor(babyBlue)
        }
    }
}

