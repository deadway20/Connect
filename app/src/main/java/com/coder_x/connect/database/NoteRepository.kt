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

    suspend fun markNoteAsCompleted(noteId: Long) {
        noteDao.markNoteAsCompleted(noteId)
    }

    suspend fun markNoteAsActive(noteId: Long) {
        noteDao.markNoteAsActive(noteId)
    }

    suspend fun deleteNoteById(noteId: Long) {
        noteDao.deleteNoteById(noteId)
    }

    fun getActiveNotes(): LiveData<List<NoteEntity>> {
        return noteDao.getActiveNotes()
    }

    fun getCompletedNotes(): LiveData<List<NoteEntity>> {
        return noteDao.getCompletedNotes()
    }

    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }




}