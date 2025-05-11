package com.coder_x.connect.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application){

    private val noteRepository: NoteRepository
    val allNotes: LiveData<List<NoteEntity>>

        init {
            val noteDao = NoteDatabase.getDatabase(application).noteDao()
            noteRepository = NoteRepository(noteDao)
            allNotes = noteRepository.allNotes
    }
    fun insert(note: NoteEntity) = viewModelScope.launch {
        noteRepository.insert(note)
    }

    fun update(note: NoteEntity) = viewModelScope.launch {
        noteRepository.update(note)
    }

    fun delete(note: NoteEntity) = viewModelScope.launch {
        noteRepository.delete(note)
    }

    fun getNoteById(noteId: Long): LiveData<NoteEntity?> {
        val result = MutableLiveData<NoteEntity?>()
        viewModelScope.launch {
            result.value = noteRepository.getNoteById(noteId)
        }
        return result
    }

}