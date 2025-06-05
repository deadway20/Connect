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
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): NoteEntity?

    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>


    @Query("SELECT * FROM notes WHERE selected_date IS NULL OR selected_date = :date ORDER BY timestamp DESC")
    fun getNotesWithDefaultDate(date: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE selected_date = :date ORDER BY timestamp DESC")
    fun getNotesByDate(date: String): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isCompleted = 0 ORDER BY timestamp DESC")
    fun getActiveNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isCompleted = 1 ORDER BY timestamp DESC")
    fun getCompletedNotes(): Flow<List<NoteEntity>>

    @Query("UPDATE notes SET isCompleted = 1 WHERE id = :noteId")
    suspend fun markNoteAsCompleted(noteId: Long)

    @Query("UPDATE notes SET isCompleted = 0 WHERE id = :noteId")
    suspend fun markNoteAsUncompleted(noteId: Long)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)

    @Query("DELETE FROM notes WHERE isCompleted = 1")
    suspend fun deleteCompletedNotes()

    @Query("DELETE FROM notes WHERE selected_date = :date")
    suspend fun deleteNotesByDate(date: String)


    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()











}