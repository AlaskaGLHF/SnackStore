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

    @Query("""
        SELECT Goods.* FROM Goods
        INNER JOIN Favorite_Goods ON Goods.id = Favorite_Goods.goods_id
        WHERE Favorite_Goods.client_id = :clientId
    """)
    fun getFavoriteGoodsWithInfo(clientId: Int): Flow<List<Goods>>

    @Query("SELECT * FROM Goods WHERE id = :id")
    suspend fun getGoodById(id: Int): Goods?


        @Query("SELECT * FROM goods WHERE name LIKE :query")
        suspend fun searchByName(query: String): List<Goods>


}


