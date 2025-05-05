package com.coder_x.connect

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.Shader.TileMode
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val fontResId =
            resources.getIdentifier("angelos", "font", requireContext().packageName)
        binding.topBarTitle.typeface = resources.getFont(fontResId)
        // make gradient text white and blue
        val paint = binding.topBarTitle.paint
        val width = paint.measureText(binding.topBarTitle.text.toString())
        val shader: Shader = LinearGradient(
            0f, 0f, width, binding.topBarTitle.textSize, intArrayOf(
                Color.WHITE,
                Color.BLUE
            ), null, TileMode.CLAMP
        )
        binding.topBarTitle.paint.shader = shader



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

}

