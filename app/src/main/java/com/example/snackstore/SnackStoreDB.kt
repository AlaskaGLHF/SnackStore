package com.example.snackstore

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.snackstore.DAO.CartDao
import com.example.snackstore.DAO.ClientsDao
import com.example.snackstore.DAO.DeliveryDriverDao
import com.example.snackstore.DAO.FavoriteGoodsDao
import com.example.snackstore.DAO.GoodsDao
import com.example.snackstore.DAO.GoodsTagsDao
import com.example.snackstore.DAO.OrderedGoodsDao
import com.example.snackstore.DAO.OrdersDao
import com.example.snackstore.entity.CartItem
import com.example.snackstore.entity.Client
import com.example.snackstore.entity.DeliveryDriver
import com.example.snackstore.entity.FavoriteGoods
import com.example.snackstore.entity.Goods
import com.example.snackstore.entity.GoodsTags
import com.example.snackstore.entity.Order
import com.example.snackstore.entity.OrderedGoods

@Database(
    entities = [
        Client::class,
        Goods::class,
        FavoriteGoods::class,
        Order::class,
        OrderedGoods::class,
        DeliveryDriver::class,
        GoodsTags::class,
        CartItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SnackStoreDatabase : RoomDatabase() {

    abstract fun clientsDao(): ClientsDao
    abstract fun goodsDao(): GoodsDao
    abstract fun favoriteGoodsDao(): FavoriteGoodsDao
    abstract fun ordersDao(): OrdersDao
    abstract fun orderedGoodsDao(): OrderedGoodsDao
    abstract fun deliveryDriverDao(): DeliveryDriverDao
    abstract fun goodsTagsDao(): GoodsTagsDao
    abstract fun cartDao(): CartDao

    companion object {
        @Volatile
        private var INSTANCE: SnackStoreDatabase? = null

        fun getDatabase(context: Context): SnackStoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SnackStoreDatabase::class.java,
                    "snack_store_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
