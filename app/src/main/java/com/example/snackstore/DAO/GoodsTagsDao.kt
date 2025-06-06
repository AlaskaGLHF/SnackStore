package com.example.snackstore.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snackstore.entity.GoodsTags

@Dao
interface GoodsTagsDao {
    @Query("SELECT * FROM Goods_Tags WHERE good_id = :goodId")
    suspend fun getTagsByGood(goodId: Int): List<GoodsTags>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goodsTag: GoodsTags)

    @Delete
    suspend fun delete(goodsTag: GoodsTags)
}