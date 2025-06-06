package com.example.snackstore.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snackstore.entity.DeliveryDriver

@Dao
interface DeliveryDriverDao {
    @Query("SELECT * FROM Delivery_Driver")
    suspend fun getAll(): List<DeliveryDriver>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(driver: DeliveryDriver)

    @Delete
    suspend fun delete(driver: DeliveryDriver)
}