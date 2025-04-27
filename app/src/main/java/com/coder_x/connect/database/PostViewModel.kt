package com.coder_x.connect.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PostViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository
    private val allPosts: LiveData<List<PostEntity>>

    init {
        val postDao = PostDatabase.getDatabase(application).postDao()
        repository = PostRepository(postDao)
        allPosts = repository.allPosts
    }

    fun insert(post: PostEntity) = viewModelScope.launch {
        repository.insert(post)
    }

    fun update(post: PostEntity) = viewModelScope.launch {
        repository.update(post)
    }

    fun delete(post: PostEntity) = viewModelScope.launch {
        repository.delete(post)
    }

    fun incrementLikes(postId: Long) = viewModelScope.launch {
        repository.incrementLikes(postId)
    }

    fun incrementComments(postId: Long) = viewModelScope.launch {
        repository.incrementComments(postId)
    }

    fun getPostById(postId: Long): LiveData<PostEntity?> {
        val result = MutableLiveData<PostEntity?>()
        viewModelScope.launch {
            result.value = repository.getPostById(postId)
        }
        return result
    }
}
