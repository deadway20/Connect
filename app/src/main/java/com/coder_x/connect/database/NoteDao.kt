package com.coder_x.connect.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity)

    @Update
    suspend fun updateTask(note: NoteEntity)

    @Delete
    suspend fun deleteTask(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getTasksById(noteId: Long): NoteEntity?

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllTasks(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE selected_date IS NULL OR selected_date = :date ORDER BY timestamp DESC")
    fun getTasksWithDefaultDate(date: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE selected_date = :date ORDER BY timestamp DESC")
    fun getTasksByDate(date: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isCompleted = :completed ORDER BY timestamp DESC")
    fun getTasksByCompletion(completed: Boolean): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE selected_date = :date AND isCompleted = 0 ORDER BY timestamp DESC")
    fun getActiveTasksByDate(date: String): Flow<List<NoteEntity>>

    @Query("UPDATE notes SET isCompleted = 1 WHERE id = :noteId")
    suspend fun markNoteAsCompleted(noteId: Long)

    @Query("UPDATE notes SET isCompleted = 0 WHERE id = :noteId")
    suspend fun markNoteAsUncompleted(noteId: Long)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteTaskById(noteId: Long)

    @Query("DELETE FROM notes WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks()

    @Query("DELETE FROM notes WHERE selected_date = :date")
    suspend fun deleteTasksByDate(date: String)


    @Query("DELETE FROM notes")
    suspend fun deleteAllTasks()

    @Query("SELECT COUNT(*) FROM notes WHERE selected_date = :date")
    fun getTasksCountByDate(date: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM notes WHERE isCompleted = 0")
    fun getActiveTasksCount(): Flow<Int>


    @Query("SELECT color FROM notes WHERE id = :itemId")
    suspend fun getColor(itemId: Int): Int? // Make it suspend, returns nullable Int

    @Query("UPDATE notes SET color = :color WHERE id = :itemId")
    suspend fun setColorView(color: Int, itemId: Long) // Changed to Long

    @Query("SELECT color FROM notes WHERE id = :itemId")
    fun getColorLiveData(itemId: Long): Flow<Int?> // Changed to Long

    // set and








}