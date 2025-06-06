package com.example.snackstore.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Delivery_Driver")
data class DeliveryDriver(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val first_name: String?,
    val second_name: String?,
    val third_name: String?,
    val phone_number: String?,
    val email: String?
)