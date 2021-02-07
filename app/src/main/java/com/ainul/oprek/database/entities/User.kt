package com.ainul.oprek.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_user")
data class User(
    val email: String,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val username: String,
    @ColumnInfo(name ="profile_picture")
    val profilePicture: String?,
    val company: String?,
    val pin: Int
)