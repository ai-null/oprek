package com.ainul.oprek.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class Status(val status: String) {
    PROGRESS("progress"),
    DONE("done"),
    CANCEL("cancel")
}

@Entity(tableName = "table_user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = 0,
    val username: String,
    val email: String,
    val pin: Int
)

@Entity(tableName = "table_project")
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = 0,
    @ColumnInfo(name = "user_id")
    @ForeignKey(
        entity = User::class,
        childColumns = ["userId"],
        parentColumns = ["id"],
        onDelete = ForeignKey.CASCADE
    )
    val userId: Int,
    @ColumnInfo(name = "customer_name")
    val customerName: String,
    val status: Status = Status.PROGRESS,
    val description: String?,
    val brand: String?,
    @ColumnInfo(name = "due_date")
    val dueDate: String?,
    val cost: Int? = 0
)