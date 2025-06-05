package com.coder_x.connect.database
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String, // variable storing the todo text (text based)
    val timestamp: Long, // variable storing the timestamp of the todo creation
    val isAudio: Boolean, // whether the todo is text based or audio based
    val isCompleted: Boolean, // variable storing the todo completion status
    val audioDuration: Long? = null, // variable storing the voice note duration (for audio based todos)
    val audioProgress: Long? = null, // variable storing the voice note progress (for audio based todos)
    val audioPath: String? = null, // variable storing the voice note path (for audio based todos)
    val type: String,  // variable storing the todo type (text or voice)
    @ColumnInfo(name = "selected_date") val selectedDate: String? = null
)
