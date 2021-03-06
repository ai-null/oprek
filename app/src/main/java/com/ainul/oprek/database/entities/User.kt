package com.ainul.oprek.database.entities

import android.os.Parcel
import android.os.Parcelable
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
    val pin: Int,
    @ColumnInfo(name = "income", defaultValue = "0.0")
    val income: Double = 0.0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeLong(id)
        parcel.writeString(username)
        parcel.writeString(profilePicture)
        parcel.writeString(company)
        parcel.writeInt(pin)
        parcel.writeDouble(income)
    }

    override fun toString(): String {
        return "User(id=$id, email=$email, username=$username, " +
                "profilePicture=$profilePicture, company=$company, " +
                "pin=REDACTED, income=$income)"
    }

    fun addIncome(values: Double): Double {
        return values + income
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}