package com.coder_x.connect

import android.view.LayoutInflater
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coder_x.connect.database.PostEntity
import com.coder_x.connect.databinding.ItemCreatePostBinding
import com.coder_x.connect.databinding.ItemPostBinding
import java.util.Date


class PostAdapter(
    private var items: List<SocialItem>,
    private val listener: OnSocialActionListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_EMPLOYEE_STATUS = 0
        const val TYPE_CREATE_POST = 1
        const val TYPE_POST = 2
    }

    // ViewHolders
    inner class EmployeeStatusViewHolder(view: View) : RecyclerView.ViewHolder(view)
    inner class CreatePostViewHolder(val binding: ItemCreatePostBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)
    interface OnSocialActionListener {
        fun onCreatePostClicked()
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is SocialItem.EmployeeStatus -> TYPE_EMPLOYEE_STATUS
        is SocialItem.CreatePost -> TYPE_CREATE_POST
        is SocialItem.Post -> TYPE_POST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_EMPLOYEE_STATUS -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_emp_status, parent, false)
                EmployeeStatusViewHolder(view)
            }

            TYPE_CREATE_POST -> {
                val binding = ItemCreatePostBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CreatePostViewHolder(binding)
            }

            else -> {
                val binding =
                    ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is SocialItem.EmployeeStatus -> {
                // هنا تربط الريسايكلر الداخلي
//                val recyclerView = (holder.itemView as RecyclerView)
//                recyclerView.adapter = EmpStatusAdapter() // افترضنا اسم الأدابتر ده
//                recyclerView.layoutManager =
//                LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            }

            is SocialItem.CreatePost -> {
                val binding = (holder as CreatePostViewHolder).binding
                binding.tvWhatOnMind.setOnClickListener {
                    listener.onCreatePostClicked()
                }
                binding.profileImage.setOnClickListener {
                    listener.onCreatePostClicked()
                }
                val context = holder.itemView.context
                val prefsHelper = SharedPrefsHelper(context)

                val imagePath = prefsHelper.getEmpImagePath()
                if (imagePath != null) {
                    try {
                        binding.profileImage.setImageURI(imagePath.toUri())
                    } catch (e: Exception) {
                        binding.profileImage.setImageResource(R.drawable.emp_img)
                    }
                }


            }

            is SocialItem.Post -> {
                val binding = (holder as PostViewHolder).binding
                val post = item.postEntity

                if(!post.postImagePath.isNullOrEmpty()){
                    try{
                        binding.imageProfile.setImageURI(post.postImagePath.toUri())
                    }catch (e: Exception){
                        binding.imageProfile.setImageResource(R.drawable.emp_img)
                    }
                }else{
                    val context = holder.itemView.context
                    val prefsHelper = SharedPrefsHelper(context)
                    prefsHelper.getEmpImagePath()?.let {
                    }
                }

                binding.tvEmployeeName.text = post.employeeName
                binding.tvEmployeeId.text = "#${post.employeeId}"

                val now = Date()
                val timeAgo = DateUtils.getRelativeTimeSpanString(post.postTime, now.time, DateUtils.MINUTE_IN_MILLIS)
                binding.tvPostTime.text = timeAgo

                binding.postText.text = post.postText
                binding.tvLikeCount.text = "${post.likesCount} Likes"
                binding.tvCommentCount.text = "${post.commentsCount} Comments"

                if (!post.postImagePath.isNullOrEmpty()) {
                    binding.postImage.visibility = View.VISIBLE
                    Glide.with(holder.itemView.context).load(post.postImagePath).into(binding.postImage)
                } else {
                    binding.postImage.visibility = View.GONE
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(posts: List<PostEntity>) {
        val newItems = mutableListOf<SocialItem>()
        newItems.add(SocialItem.EmployeeStatus)
        newItems.add(SocialItem.CreatePost)
        newItems.addAll(posts.map { SocialItem.Post(it) })
        this.items = newItems
        notifyDataSetChanged()
    }
}
