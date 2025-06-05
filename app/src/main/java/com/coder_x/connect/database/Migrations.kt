// Migrations.kt
package com.coder_x.connect.database // أو أي package مناسب لك

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration1To2 : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE notes_new (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "timestamp INTEGER NOT NULL, " +
                    "isAudio INTEGER NOT NULL, " +
                    "isCompleted INTEGER NOT NULL, " +
                    "audioDuration INTEGER, " +
                    "audioProgress INTEGER, " +
                    "audioPath TEXT, " +
                    "type TEXT NOT NULL" +
                    ")"
        )
        db.execSQL(
            "INSERT INTO notes_new (id, title, timestamp, isAudio, isCompleted, audioDuration, audioProgress, audioPath, type) " +
                    "SELECT id, title, timestamp, isAudio, isCompleted, audioDuration, audioProgress, audioPath, type FROM notes"
        )
        db.execSQL("DROP TABLE notes")
        db.execSQL("ALTER TABLE notes_new RENAME TO notes")
    }
}
// Migration 2 to 3
object Migration2To3 : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notes ADD COLUMN selected_date TEXT DEFAULT NULL")
    }
}