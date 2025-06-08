package com.example.snackstore.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snackstore.entity.Goods
import com.example.snackstore.entity.GoodsTags
import kotlinx.coroutines.flow.Flow

@Dao
interface GoodsTagsDao {
    @Query("SELECT * FROM Goods_Tags WHERE good_id = :goodId")
    suspend fun getTagsByGood(goodId: Int): List<GoodsTags>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(goodsTags: List<GoodsTags>)

    @Delete
    suspend fun delete(goodsTag: GoodsTags)

    @Query("""
    SELECT Goods.* FROM Goods
    INNER JOIN Goods_Tags ON Goods.id = Goods_Tags.good_id
    WHERE Goods_Tags.tag = :tag
""")
    fun getGoodsByTag(tag: String): Flow<List<Goods>>

    @Query("SELECT DISTINCT tag FROM Goods_Tags WHERE tag IS NOT NULL")
    fun getAllTags(): Flow<List<String>>


}