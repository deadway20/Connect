package com.coder_x.connect.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

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

    fun getNoteById(noteId: Long) = viewModelScope.launch {
        noteRepository.getNoteById(noteId)
    }
    fun getAllNotesByDate(timestamp: Long) = viewModelScope.launch {
        noteRepository.getAllNotesByDate(timestamp)
    }

    fun getActiveNotes() = viewModelScope.launch {
        noteRepository.getActiveNotes()
    }
    fun getCompletedNotes() = viewModelScope.launch {
        noteRepository.getCompletedNotes()
    }
    fun markNoteAsCompleted(noteId: Long) = viewModelScope.launch {
        noteRepository.markNoteAsCompleted(noteId)
    }
    fun markNoteAsUncompleted(noteId: Long) = viewModelScope.launch {
        noteRepository.markNoteAsUncompleted(noteId)
    }
    fun deleteCompletedNotes() = viewModelScope.launch {
        noteRepository.deleteCompletedNotes()
    }

    fun deleteNoteById(noteId: Long) = viewModelScope.launch {
        noteRepository.deleteNoteById(noteId)
    }

    fun deleteAllNotes() = viewModelScope.launch {
        noteRepository.deleteAllNotes()
    }

    fun deleteNotesByDate(date: String) = viewModelScope.launch {
        noteRepository.deleteNotesByDate(date)
    }

}