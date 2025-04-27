package com.coder_x.connect

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coder_x.connect.database.PostEntity
import com.coder_x.connect.databinding.PostItemBinding
import java.io.File

class PostAdapter(
    private var postList: List<PostEntity>,
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    // ViewHolder class
    inner class PostViewHolder(val binding: PostItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        with(holder.binding) {
            tvEmployeeName.text = post.employeeName
            tvEmployeeId.text = "#${post.employeeId}"
            tvPostTime.text = post.postTime.toString()
            postText.text = post.postText
            tvLikeCount.text = post.likesCount.toString()
            tvCommentCount.text = post.commentsCount.toString()

            if (!post.postImagePath.isNullOrEmpty()) {
                val imageFile = File(post.postImagePath)
                postImage.visibility = View.VISIBLE
                Glide.with(holder.itemView.context)
                    .load(imageFile)
                    .into(postImage)
            } else {
                postImage.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = postList.size

    fun submitList(newList: List<PostEntity>) {
        postList = newList
        notifyDataSetChanged() // بعدين لما تحب تحسن الأداء بنخليه DiffUtil
    }
}

