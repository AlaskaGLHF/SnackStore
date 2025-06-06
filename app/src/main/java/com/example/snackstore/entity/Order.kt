package com.example.snackstore.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Orders",
    foreignKeys = [
        ForeignKey(entity = Client::class, parentColumns = ["id"], childColumns = ["client_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = DeliveryDriver::class, parentColumns = ["id"], childColumns = ["delivery_driver_id"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val client_id: Int,
    val address: String?,
    val delivery_driver_id: Int?,
    val date: String?
)