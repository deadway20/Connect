package com.coder_x.connect.todo

data class TaskItem(
    val title: String,
    val date: String,
    var isCompleted: Boolean,
)

