package com.example.snackstore.DAO

import androidx.room.Dao
import androidx.room.Embedded
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItem)

    @Query("SELECT * FROM cart_items WHERE client_id = :clientId")
    fun getCartItems(clientId: Long): Flow<List<CartItem>>

    @Query("DELETE FROM cart_items WHERE client_id = :clientId")
    suspend fun clearCart(clientId: Long)

    @Query("""
    SELECT Cart_Items.*, Goods.name AS name, Goods.price AS price 
    FROM Cart_Items 
    INNER JOIN Goods ON Cart_Items.good_id = Goods.id
    WHERE Cart_Items.client_id = :clientId
""")
    fun getDetailedCartItems(clientId: Long): Flow<List<CartItemWithInfo>>

}
data class CartItemWithInfo(
    @Embedded val item: CartItem,
    val name: String?,
    val price: String?
)

