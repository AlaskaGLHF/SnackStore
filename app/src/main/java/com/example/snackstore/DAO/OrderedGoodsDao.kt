package com.example.snackstore.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snackstore.entity.OrderedGoods
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderedGoodsDao {
    @Query("SELECT * FROM Ordered_Goods WHERE order_id = :orderId")
    suspend fun getOrderedGoodsByOrder(orderId: Int): List<OrderedGoods>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(orderedGoods: OrderedGoods)

    @Delete
    suspend fun delete(orderedGoods: OrderedGoods)

    @Query("SELECT * FROM ordered_goods WHERE order_id = :orderId")
    fun getGoodsForOrder(orderId: Int): Flow<List<OrderedGoods>>

    @Query("SELECT DISTINCT order_id FROM ordered_goods WHERE good_id IN (SELECT good_id FROM ordered_goods)")
    fun getAllOrders(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(orderedGoods: List<OrderedGoods>)


}