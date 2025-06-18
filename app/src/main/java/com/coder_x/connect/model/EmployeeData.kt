package com.coder_x.connect.model

data class EmployeeData(
    val recordDate: String,
    val clockIn: String,
    val clockOut: String,
    val totalHours: String,
    val overtimeInMinutes: String,
    val delayInMinutes: String,
    var isAttend: Boolean,
    var isAbsence: Boolean,
    var absenceCount: Int,
    var attendCount: Int
)


