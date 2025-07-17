package com.coder_x.connect.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData

class NoteRepository(private val noteDao: NoteDao) {
    
    suspend fun insert(note: NoteEntity) = noteDao.insert(note)
    
    suspend fun update(note: NoteEntity) = noteDao.updateTask(note)
    
    suspend fun delete(note: NoteEntity) = noteDao.deleteTask(note)
    
    fun getAllTasks(): LiveData<List<NoteEntity>> = noteDao.getAllTasks().asLiveData()
    fun getFavoriteTasks(): LiveData<List<NoteEntity>> = noteDao.getFavoriteTasks().asLiveData()
    fun getActiveTasks(): LiveData<List<NoteEntity>> = noteDao.getTasksByCompletion(false).asLiveData()
    fun getCompletedTasks(): LiveData<List<NoteEntity>> = noteDao.getTasksByCompletion(true).asLiveData()

    fun getActiveTasksByDate(date: String): LiveData<List<NoteEntity>> = noteDao.getActiveTasksByDate(date).asLiveData()

    suspend fun setTaskUncompleted(noteId: Long)  = noteDao.markNoteAsUncompleted(noteId)
    
    suspend fun setTaskCompleted(noteId: Long) = noteDao.markNoteAsCompleted(noteId)

    suspend fun deleteNoteById(noteId: Long) = noteDao.deleteTaskById(noteId)
    

    suspend fun deleteAllTasks() = noteDao.deleteAllTasks()

    suspend fun deleteNotesByDate(date: String) = noteDao.deleteTasksByDate(date)
    

    suspend fun deleteCompletedNotes() = noteDao.deleteCompletedTasks()
    

    fun getTasksCountByDate(date: String): LiveData<Int> {
        return noteDao.getTasksCountByDate(date).asLiveData()
    }

    fun getActiveTasksCount(): LiveData<Int> = noteDao.getActiveTasksCount().asLiveData()
    

    suspend fun setColorView(color: Int, itemId: Long) = noteDao.setColorView(color, itemId)
    
    
    fun getColorLiveData(itemId: Long): LiveData<Int?> {
        return noteDao.getColorLiveData(itemId).asLiveData()
    }
    
    suspend fun setFavoriteTask(noteId: Long, isFavorite: Boolean) {
        noteDao.setFavoriteTask(noteId, isFavorite)
    }

}