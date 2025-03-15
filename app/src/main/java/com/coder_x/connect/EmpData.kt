package com.coder_x.connect

data class EmpData(
    val recordDate: String,
    val checkInTime: String,
    val checkOutTime: String,
    val workHours: String,
    val delayInMinutes: String,
    val overtimeInMinutes: String,
    var isAbsent: Boolean

)


