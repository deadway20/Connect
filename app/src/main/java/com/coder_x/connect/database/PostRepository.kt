package com.coder_x.connect.database

import androidx.lifecycle.LiveData

class PostRepository(private val postDao: PostDao) {

    val allPosts: LiveData<List<PostEntity>> = postDao.getAllPosts()

    suspend fun insert(post: PostEntity) {
        postDao.insertPost(post)
    }

    suspend fun update(post: PostEntity) {
        postDao.updatePost(post)
    }

    suspend fun delete(post: PostEntity) {
        postDao.deletePost(post)
    }

    suspend fun incrementLikes(postId: Long) {
        postDao.incrementLikes(postId)
    }

    suspend fun incrementComments(postId: Long) {
        postDao.incrementComments(postId)
    }

    suspend fun getPostById(postId: Long): PostEntity? {
        return postDao.getPostById(postId)
    }
}
