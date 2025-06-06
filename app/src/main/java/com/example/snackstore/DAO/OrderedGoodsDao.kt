package com.example.snackstore.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snackstore.entity.OrderedGoods

@Dao
interface OrderedGoodsDao {
    @Query("SELECT * FROM Ordered_Goods WHERE order_id = :orderId")
    suspend fun getOrderedGoodsByOrder(orderId: Int): List<OrderedGoods>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(orderedGoods: OrderedGoods)

    @Delete
    suspend fun delete(orderedGoods: OrderedGoods)
}