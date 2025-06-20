package com.example.snackstore.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "Cart_Items",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["client_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Goods::class,
            parentColumns = ["id"],
            childColumns = ["good_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["client_id"]),
        Index(value = ["good_id"])
    ]
)
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val client_id: Int,
    val good_id: Int,
    val quantity: Int = 1
)

data class CartItemWithGood(
    @Embedded val cartItem: CartItem,
    @Relation(
        parentColumn = "good_id",
        entityColumn = "id"
    )
    val good: Goods
)



