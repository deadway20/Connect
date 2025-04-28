package com.coder_x.connect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.coder_x.connect.database.PostEntity
import com.coder_x.connect.database.PostViewModel
import com.coder_x.connect.databinding.FragmentSocialBinding

class SocialFragment : Fragment() {
    private lateinit var binding: FragmentSocialBinding
    private lateinit var adapter: PostAdapter
    private lateinit var fragmentManager: FragmentManager

    private val postViewModel: PostViewModel by activityViewModels() // اضفنا هنا

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSocialBinding.inflate(inflater, container, false)
        adapter = PostAdapter(emptyList())
        fragmentManager = requireActivity().supportFragmentManager
        binding.posrRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.posrRecyclerView.adapter = adapter

        // 🛠️ هنا نراقب البوستات ونعرضها لما تتغير
        postViewModel.allPosts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        binding.tvWhatOnMind.setOnClickListener {
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddPostFragment())
                .addToBackStack(null)
                .commit()
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
}
