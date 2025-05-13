package com.coder_x.connect.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update


@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): NoteEntity?


    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE date(strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) = date(strftime('%Y-%m-%d', :timestamp / 1000, 'unixepoch'))")
    suspend fun getAllNotesByDate(timestamp: Long): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isCompleted = 0 ORDER BY timestamp DESC")
    suspend fun getActiveNotes(): LiveData<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isCompleted = 1 ORDER BY timestamp DESC")
    suspend fun getCompletedNotes(): LiveData<List<NoteEntity>>

    @Query("UPDATE notes SET isCompleted = 1 WHERE id = :noteId")
    suspend fun markNoteAsCompleted(noteId: Long)

    @Query("UPDATE notes SET isCompleted = 0 WHERE id = :noteId")
    suspend fun markNoteAsUncompleted(noteId: Long)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)

    @Query("DELETE FROM notes WHERE isCompleted = 1")
    suspend fun deleteCompletedNotes()

    @Query("DELETE FROM notes WHERE date(strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch')) = :date")
    suspend fun deleteNotesByDate(date: String)


    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()











}