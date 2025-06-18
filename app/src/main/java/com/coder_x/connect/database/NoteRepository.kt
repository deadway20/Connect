package com.coder_x.connect.database

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData

class NoteRepository(private val noteDao: NoteDao) {
    val allNotes: LiveData<List<NoteEntity>> = noteDao.getAllTasks().asLiveData()


    suspend fun insert(note: NoteEntity) {
        Log.d("NOTE_REPO", "Inserting: $note")
        noteDao.insert(note)
    }

    suspend fun update(note: NoteEntity) {
        noteDao.updateTask(note)
    }

    suspend fun delete(note: NoteEntity) {
        noteDao.deleteTask(note)
    }

    suspend fun getNoteById(noteId: Long): NoteEntity? {
        return noteDao.getTasksById(noteId)
    }

    fun getTasksByDate(date: String): LiveData<List<NoteEntity>> {
        return noteDao.getTasksByDate(date).asLiveData()
    }

    fun getTasksWithDefaultDate(date: String): LiveData<List<NoteEntity>> {
        return noteDao.getTasksWithDefaultDate(date).asLiveData()
    }

    fun getAllTasks(): LiveData<List<NoteEntity>> = noteDao.getAllTasks().asLiveData()

    fun getCompletedTasks(): LiveData<List<NoteEntity>> =
        noteDao.getTasksByCompletion(true).asLiveData()

    fun getActiveTasks(): LiveData<List<NoteEntity>> =
        noteDao.getTasksByCompletion(false).asLiveData()


    fun getActiveTasksByDate(date: String): LiveData<List<NoteEntity>> {
        return noteDao.getActiveTasksByDate(date).asLiveData()
    }

    suspend fun setTaskUncompleted(noteId: Long) {
        noteDao.markNoteAsUncompleted(noteId)
    }

    suspend fun setTaskCompleted(noteId: Long) {
        noteDao.markNoteAsCompleted(noteId)
    }

    suspend fun deleteNoteById(noteId: Long) {
        noteDao.deleteTaskById(noteId)
    }

    suspend fun deleteAllTasks() {
        noteDao.deleteAllTasks()
    }

    suspend fun deleteNotesByDate(date: String) {
        noteDao.deleteTasksByDate(date)
    }

    suspend fun deleteCompletedNotes() {
        noteDao.deleteCompletedTasks()
    }

    fun getTasksCountByDate(date: String): LiveData<Int> {
        return noteDao.getTasksCountByDate(date).asLiveData()
    }

    fun getActiveTasksCount(): LiveData<Int> {
        return noteDao.getActiveTasksCount().asLiveData()
    }

    suspend fun setColorView(color: Int, itemId: Long) {
        noteDao.setColorView(color, itemId)
    }

    suspend fun getColor(itemId: Int): Int? {
        return noteDao.getColor(itemId)
    }

    fun getColorLiveData(itemId: Long): LiveData<Int?> {
        return noteDao.getColorLiveData(itemId).asLiveData()
    }


}