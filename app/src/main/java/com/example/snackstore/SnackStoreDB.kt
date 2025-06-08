package com.example.snackstore

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.snackstore.DAO.*
import com.example.snackstore.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Log.d("SnackStore", "База данных создана. Начинается предзаполнение...")
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { prepopulateDatabase(it) }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepopulateDatabase(db: SnackStoreDatabase) {
            val clientsDao = db.clientsDao()
            val goodsDao = db.goodsDao()
            val favoriteDao = db.favoriteGoodsDao()
            val tagsDao = db.goodsTagsDao()
            val ordersDao = db.ordersDao()
            val orderedGoodsDao = db.orderedGoodsDao()
            val deliveryDao = db.deliveryDriverDao()
            val cartDao = db.cartDao()

            val client = Client(1, "Иван", "Иванов", "Иванович", "1990-01-01", "+71234567890", "ivan@example.com", "password123")
            val driver = DeliveryDriver(1, "Петр", "Петров", "Петрович", "+79876543210", "driver@example.com")

            val goodsList = listOf(
                Goods(1, "Чипсы", "Картофельные чипсы", "89", "chip.jpg", 10),
                Goods(2, "Сухарики", "Ржаные сухарики", "49", "croutons.jpg", 0),
                Goods(3, "Газировка", "Газированный напиток", "99", "soda.jpg", 15),
                Goods(4, "Сок", "Фруктовый сок", "79", "juice.jpg", 5),
                Goods(5, "Шоколад", "Молочный шоколад", "69", "chocolate.jpg", 0),
                Goods(6, "Орехи", "Ассорти орехов", "120", "nuts.jpg", 20),
                Goods(7, "Печенье", "Овсяное печенье", "60", "cookies.jpg", 0),
                Goods(8, "Вафли", "Шоколадные вафли", "55", "waffles.jpg", 10),
                Goods(9, "Энергетик", "Напиток энергетический", "110", "energy.jpg", 25),
                Goods(10, "Минералка", "Минеральная вода", "45", "water.jpg", 0)
            )

            val favorite = FavoriteGoods(1, 1, 1)
            val tag = GoodsTags(1, 1, "Снэк")
            val order = Order(1, 1, "ул. Пушкина, д. 10", 1, "2025-06-08")
            val ordered = OrderedGoods(1, 1, 2)
            val cartItem = CartItem(1, 1, 3, 2)

            Log.d("SnackStore", "Добавление клиента...")
            clientsDao.insertClient(client)

            Log.d("SnackStore", "Добавление водителя доставки...")
            deliveryDao.insert(driver)

            Log.d("SnackStore", "Добавление товаров...")
            goodsList.forEach { good ->
                goodsDao.insert(good)
                Log.d("SnackStore", "Добавлен товар: ${good.name}")
            }

            Log.d("SnackStore", "Добавление избранного...")
            favoriteDao.insert(favorite)

            Log.d("SnackStore", "Добавление тега...")
            tagsDao.insert(tag)

            Log.d("SnackStore", "Добавление заказа...")
            ordersDao.insert(order)

            Log.d("SnackStore", "Добавление заказа-товара...")
            orderedGoodsDao.insert(ordered)

            Log.d("SnackStore", "Добавление товара в корзину...")
            cartDao.addToCart(cartItem)

            Log.d("SnackStore", "Предзаполнение завершено.")
        }
        }
    }
