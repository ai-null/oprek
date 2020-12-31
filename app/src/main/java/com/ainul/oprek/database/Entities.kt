package com.ainul.oprek.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class Status(val value: Int) {
    PROGRESS(0),
    DONE(1),
    CANCEL(-1);
}

@Entity(tableName = "table_user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val username: String,
    val email: String,
    val pin: Int
)

@Entity(tableName = "table_project")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "user_id")
    @ForeignKey(
        entity = User::class,
        childColumns = ["userId"],
        parentColumns = ["id"],
        onDelete = ForeignKey.CASCADE
    )
    val userId: Long,
    @ColumnInfo(name="device_name")
    val deviceName: String,
    @ColumnInfo(name = "customer_name")
    val customerName: String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: Int? = 0,
    val status: Int = Status.PROGRESS.value,
    val description: String?,
    @ColumnInfo(name = "due_date")
    val dueDate: String?,
    val cost: Double? = 0.0
)