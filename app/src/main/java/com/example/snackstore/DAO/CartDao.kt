package com.example.snackstore.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.snackstore.entity.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cartItem: CartItem)

    @Query("DELETE FROM Cart_Items WHERE id = :id")
    suspend fun removeFromCartById(id: Int)

    @Query("DELETE FROM Cart_Items WHERE client_id = :clientId")
    suspend fun clearCartForClient(clientId: Int)

    @Query("SELECT * FROM Cart_Items WHERE client_id = :clientId")
    fun getCartItemsForClient(clientId: Int): Flow<List<CartItem>>

    @Query("UPDATE Cart_Items SET quantity = :quantity WHERE id = :id")
    suspend fun updateQuantity(id: Int, quantity: Int)
}
