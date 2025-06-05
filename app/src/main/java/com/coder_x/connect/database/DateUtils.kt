package com.coder_x.connect.database

import java.util.Locale

object DateUtils {
    fun formatDate(day: Int, month: Int, year: Int): String {
        return String.format(Locale.getDefault(), "%02d/%02d/%04d", day, month + 1, year)
    }
}