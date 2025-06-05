package com.coder_x.connect.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<NoteEntity>> = noteDao.getAllNotes().asLiveData()



    suspend fun insert(note: NoteEntity) {
        Log.d("NOTE_REPO", "Inserting: $note")
        noteDao.insert(note)
    }

    suspend fun update(note: NoteEntity) {
        noteDao.updateNote(note)
    }

    suspend fun delete(note: NoteEntity) {
        noteDao.deleteNote(note)
    }

    suspend fun getNoteById(noteId: Long): NoteEntity? {
        return noteDao.getNoteById(noteId)
    }

    suspend  fun getNotesByDate(date: String): LiveData<List<NoteEntity>> {
        return noteDao.getNotesByDate(date).asLiveData()
    }
     fun getNotesWithDefaultDate(date: String): LiveData<List<NoteEntity>> {
        return noteDao.getNotesWithDefaultDate(date).asLiveData()
    }
    suspend fun getActiveNotes(): LiveData<List<NoteEntity>> {
        return noteDao.getActiveNotes().asLiveData()
    }

    suspend fun getCompletedNotes(): LiveData<List<NoteEntity>> {
        return noteDao.getCompletedNotes().asLiveData()
    }

    suspend fun markNoteAsUncompleted(noteId: Long) {
        noteDao.markNoteAsUncompleted(noteId)
    }

    suspend fun markNoteAsCompleted(noteId: Long) {
        noteDao.markNoteAsCompleted(noteId)
    }

    suspend fun deleteNoteById(noteId: Long) {
        noteDao.deleteNoteById(noteId)
    }

    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }

    suspend fun deleteNotesByDate(date: String) {
        noteDao.deleteNotesByDate(date)
    }

    suspend fun deleteCompletedNotes() {
        noteDao.deleteCompletedNotes()
    }
}