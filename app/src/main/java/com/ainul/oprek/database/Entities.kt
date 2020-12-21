package com.ainul.oprek.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class Status(private val status: String) {
    PROGRESS("progress"),
    DONE("done"),
    CANCEL("cancel");
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
    @ColumnInfo(name = "customer_name")
    val customerName: String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: Int? = 0,
    val status: String = Status.PROGRESS.name,
    val description: String?,
    val brand: String?,
    @ColumnInfo(name = "due_date")
    val dueDate: String?,
    val cost: Double? = 0.0
)