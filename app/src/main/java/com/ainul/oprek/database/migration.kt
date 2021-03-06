package com.ainul.oprek.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_10_11 = object : Migration(10, 11) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE table_user ADD COLUMN income REAL DEFAULT 0.0 NOT NULL")
    }

}