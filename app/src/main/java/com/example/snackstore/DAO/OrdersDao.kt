package com.example.snackstore.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snackstore.entity.Order

@Dao
interface OrdersDao {
    @Query("SELECT * FROM Orders WHERE client_id = :clientId")
    suspend fun getOrdersByClient(clientId: Int): List<Order>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)

    @Delete
    suspend fun delete(order: Order)
}