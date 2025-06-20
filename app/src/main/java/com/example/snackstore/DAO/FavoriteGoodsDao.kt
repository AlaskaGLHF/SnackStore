package com.example.snackstore.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snackstore.entity.FavoriteGoods


@Dao
interface FavoriteGoodsDao {

    @Query("SELECT * FROM Favorite_Goods WHERE client_id = :clientId")
    suspend fun getFavoritesByClient(clientId: Int): List<FavoriteGoods>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteGoods: FavoriteGoods)

    @Delete
    suspend fun delete(favoriteGoods: FavoriteGoods)

    @Query("SELECT * FROM Favorite_Goods WHERE client_id = :clientId AND goods_id = :goodsId LIMIT 1")
    suspend fun getFavoriteByClientAndGoods(clientId: Int, goodsId: Int): FavoriteGoods?

    @Query("DELETE FROM Favorite_Goods WHERE client_id = :clientId AND goods_id = :goodsId")
    suspend fun deleteByClientAndGoods(clientId: Int, goodsId: Int)
}
