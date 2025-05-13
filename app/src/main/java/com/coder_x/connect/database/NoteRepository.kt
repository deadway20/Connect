package com.coder_x.connect.database

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<NoteEntity>> = noteDao.getAllNotes()

    suspend fun insert(note: NoteEntity) {
        noteDao.insertNote(note)
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

    suspend fun getAllNotesByDate(timestamp: Long): LiveData<List<NoteEntity>> {
        return noteDao.getAllNotesByDate(timestamp)
    }

    suspend fun getActiveNotes(): LiveData<List<NoteEntity>> {
        return noteDao.getActiveNotes()
    }

    suspend fun getCompletedNotes(): LiveData<List<NoteEntity>> {
        return noteDao.getCompletedNotes()
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