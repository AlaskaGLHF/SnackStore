package com.example.snackstore.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snackstore.entity.Goods
import kotlinx.coroutines.flow.Flow


@Dao
interface GoodsDao {
    @Query("SELECT * FROM Goods")
    fun getAllGoods(): Flow<List<Goods>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goods: Goods)

    @Delete
    suspend fun delete(goods: Goods)
}