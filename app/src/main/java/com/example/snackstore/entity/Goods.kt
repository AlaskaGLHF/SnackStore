package com.example.snackstore.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Goods")
data class Goods(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String?,
    val description: String?,
    val price: String?,
    val image_path: String?,
    val discount: Int?
)