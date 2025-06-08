package com.example.snackstore.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(
    tableName = "Ordered_Goods",
    foreignKeys = [
        ForeignKey(entity = Order::class, parentColumns = ["id"], childColumns = ["order_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Goods::class, parentColumns = ["id"], childColumns = ["good_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class OrderedGoods(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val order_id: Int,
    val good_id: Int,
    val full_price: Int,
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)