package com.example.snackstore.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.snackstore.entity.Goods
import com.example.snackstore.entity.Order
import com.example.snackstore.entity.OrderedGoods
import kotlinx.coroutines.flow.Flow

@Dao
interface OrdersDao {
    @Query("SELECT * FROM Orders WHERE client_id = :clientId")
    suspend fun getOrdersByClient(clientId: Int): List<Order>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: Order)

    @Delete
    suspend fun delete(order: Order)

    @Transaction
    @Query("SELECT * FROM Orders WHERE client_id = :clientId ORDER BY date DESC")
    fun getOrdersWithGoods(clientId: Int): Flow<List<OrderWithOrderedGoods>>

}

data class OrderedGoodWithInfo(
    @Embedded val orderedGood: OrderedGoods,
    @Relation(
        parentColumn = "good_id",
        entityColumn = "id"
    )
    val good: Goods
)

data class OrderWithOrderedGoods(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "id",
        entityColumn = "order_id",
        entity = OrderedGoods::class
    )
    val orderedGoods: List<OrderedGoodWithInfo>
)