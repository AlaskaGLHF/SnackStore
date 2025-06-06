package com.example.snackstore.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey


@Entity(tableName = "Clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val first_name: String?,
    val second_name: String?,
    val third_name: String?,
    val birth_day: String?,
    val phone_number: String?,
    val email: String?,
    val password: String?
)
