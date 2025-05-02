package com.coder_x.connect.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val employeeName: String,
    val employeeId: Int,
    val postTime: Long,
    val postText: String?,      // ممكن يكون null
    val postImagePath: String?, // ممكن يكون null
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean,
    val employeeImage: String? = null
)
