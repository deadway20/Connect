package com.coder_x.connect.database


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String, // variable storing the todo text (text based)
    val isAudio: Boolean, // whether the todo is text based or audio based
    val isCompleted: Boolean, // هل اكتملت المهمة ام لا
    val audioName: String? = null, // variable storing the voice note sound (for audio based todos)
    val audioDuration: Long? = null, // variable storing the voice note duration (for audio based todos)
    val audioPath: String? = null, // variable storing the voice note path (for audio based todos)
    val audioProgress: Long? = null, // variable storing the voice note progress (for audio based todos)
    val totalAudioTime: Long? = null, // الوقت الكلي للفويس (للتودو الصوتية)
    val timestamp: Long, // وقت تسجيل الفويس (للتودو الصوتية) او وقت انشاء النوتة النصية
)
