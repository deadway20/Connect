package com.coder_x.connect.database

import androidx.lifecycle.LiveData

class PostRepository(private val postDao: PostDao) {

    val allPosts: LiveData<List<PostData>> = postDao.getAllPosts()

    suspend fun insert(post: PostData) {
        postDao.insertPost(post)
    }

    suspend fun update(post: PostData) {
        postDao.updatePost(post)
    }

    suspend fun delete(post: PostData) {
        postDao.deletePost(post)
    }

    suspend fun incrementLikes(postId: Long) {
        postDao.incrementLikes(postId)
    }

    suspend fun incrementComments(postId: Long) {
        postDao.incrementComments(postId)
    }

    suspend fun getPostById(postId: Long): PostData? {
        return postDao.getPostById(postId)
    }
}
