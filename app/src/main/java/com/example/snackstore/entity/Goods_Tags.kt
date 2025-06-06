package com.example.snackstore.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Goods_Tags",
    foreignKeys = [
        ForeignKey(entity = Goods::class, parentColumns = ["id"], childColumns = ["good_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class GoodsTags(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val good_id: Int,
    val tag: String?
)