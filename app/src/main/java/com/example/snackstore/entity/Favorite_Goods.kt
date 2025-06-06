package com.example.snackstore.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Favorite_Goods",
    foreignKeys = [
        ForeignKey(entity = Client::class, parentColumns = ["id"], childColumns = ["client_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Goods::class, parentColumns = ["id"], childColumns = ["goods_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class FavoriteGoods(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val client_id: Int,
    val goods_id: Int
)