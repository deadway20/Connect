package com.coder_x.connect

data class TodoData(
    val id: Long, // Unique identifier for the todo item
    val todoTitle: String, // Variable storing the todo text (text or voice)
    val todoTime: String, // Variable storing the todo creation time
    val isCompleted: Boolean, // Variable storing the todo completion status
    val audioPath: String?, // Variable storing the voice note path (for audio based todos)
    val totalDuration: Long?,  // Variable storing the voice note duration (for audio based todos)
    val progress: Long?, // Variable storing the voice note progress (for audio based todos)
    val type: TodoType, // Variable storing the todo type (text or voice)
    val selectedDate: String? // Variable storing the selected date (for calendar based todos)
)

enum class TodoType {
    TEXT, // Text based
    VOICE // Voice based
}

