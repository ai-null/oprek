package com.ainul.oprek.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ainul.oprek.util.Constants

@Entity(
    tableName = "table_project",
    foreignKeys = [ForeignKey(
        entity = User::class,
        childColumns = ["user_id"],
        parentColumns = ["id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Project(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    @ColumnInfo(name = "user_id", index = true)
    val userId: Long,
    @ColumnInfo(name = "device_img")
    val deviceImage: String?,
    @ColumnInfo(name = "device_name")
    val deviceName: String,
    @ColumnInfo(name = "customer_name")
    val customerName: String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String?,
    val status: Int = Constants.Status.PROGRESS.value,
    val description: String?,
    @ColumnInfo(name = "due_date")
    val dueDate: String?,
    val cost: Double = 0.0
)