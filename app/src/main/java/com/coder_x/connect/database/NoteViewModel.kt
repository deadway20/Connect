package com.coder_x.connect.database

import android.app.Application
import androidx.lifecycle.switchMap
import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val noteRepository: NoteRepository
    val allNotes: LiveData<List<NoteEntity>>
    val notesByDate: LiveData<List<NoteEntity>>

    private val _selectedDate = MutableLiveData<String>().apply {
        value = getCurrentDate()
    }
    val selectedDate: LiveData<String> = _selectedDate

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        noteRepository = NoteRepository(noteDao)
        allNotes = noteRepository.allNotes

        notesByDate = selectedDate.switchMap { date ->
            noteRepository.getNotesWithDefaultDate(date)
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
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