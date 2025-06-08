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
                            Log.d("SnackStore", "БД создана. Предзаполнение...")
                            // Используем instance напрямую!
                            CoroutineScope(Dispatchers.IO).launch {
                                prepopulateDatabase(INSTANCE!!)
                            }
                        }
                    })
                    .fallbackToDestructiveMigration(true)
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
                Goods(1, "Напиток Cola «Черноголовка» без сахара газированный 1,5", "Газированный напиток", "135", "blackhead.jpeg", 10),
                Goods(2, "Напиток Frustyle Огненное манго со вкусом манго и перца табаско газированный 1л", "Газированный напиток", "130", "frustylemango.jpeg", 10),
                Goods(3, "Напиток CoolCola газированный 1,5л", "Газированный напиток", "145", "coolcola.jpeg", 10),
                Goods(4, "Напиток Coca-Cola Zero газированный 1л", "Газированный напиток", "139", "cocacola.jpeg", 10),
                Goods(5, "Напиток Fancy газированный 1,5л", "Газированный напиток", "111", "fancy.jpeg", 10),
                Goods(6, "Напиток Street газированный 1,5л", "Газированный напиток", "145", "street.jpeg", 10),
                Goods(7, "Напиток Pepsi-Cola газированный 0,33л", "Газированный напиток", "100", "pepsi.jpeg", 10),
                Goods(8, "Чипсы Lay's Рифлёные сметана и лук, 140г", "Чипсы", "150", "laysrifle.jpg", 10),
                Goods(9, "Чипсы Lit Energy сахалинский краб, 120г", "Чипсы", "170", "litnergycrab.jpg", 10),
                Goods(10, "Чипсы Pringles сметана и лук, 165г", "Чипсы", "300", "pringles.jpg", 10),
                Goods(11, "Чипсы Lay's Flaming Hot острые креветка васаби, 105г", "Чипсы", "160", "laysflaminhot.jpg", 10),
                Goods(12, "Чипсы MAMEE Ghost Pepper супер острые", "Чипсы", "650", "ghostpepper.jpg", 10),
                Goods(18, "Байкал Natural Energy Земляника-Вишня, 450мл", "Энергетический напиток", "81", "baikalenergy.jpg", 10),
                Goods(19, "Adrenaline Rush, 449мл", "Энергетический напиток", "130", "adrenalinerush.jpg", 10),
                Goods(20, "Red Bull, 250мл", "Энергетический напиток", "130", "redbull.jpg", 10),
                Goods(21, "Энергетический напиток Gorilla Original, 450мл", "Энергетический напиток", "90", "gorilla.jpg", 10),
                Goods(22, "Напиток Volt Energy Вишня-кола, 450мл", "Энергетический напиток", "90", "volt.jpg", 10),
                Goods(23, "RIOBA Сок томатный с солью, 250мл", "Сок", "55", "riobatom.jpg", 10),
                Goods(24, "Сок J7 яблоко-персик с мякотью, 970мл", "Сок", "120", "j7.jpg", 10),
                Goods(25, "Сок ФрутоНяня яблоко-ананас, 200мл", "Сок", "80", "frutonana.jpg", 10),
                Goods(26, "Сок Сады Придонья яблоко-груша, 1л", "Сок", "90", "sokgarden.jpg", 10),
                Goods(27, "Сок Rich Грейпфрут, 1л", "Сок", "150", "richgrep.jpg", 10),
                Goods(28, "Морс ФрутоНяня Клюква, черника и вишня, 200мл", "Морс", "60", "morsclukva.jpg", 10),
                Goods(29, "Морс Сады Придонья с клюквой, 1л", "Морс", "150", "morsgarden.jpg", 10),
                Goods(30, "Морс Чудо-ягода Ягодный, 970мл", "Морс", "230", "morsberry.jpg", 10),
                Goods(31, "Морс Добрый Брусника+Морошка, 970мл", "Морс", "130", "morsgood.jpg", 10),
                Goods(32, "Морс Облепиховый, 500мл", "Морс", "90", "morsobl.jpg", 10),
                Goods(33, "Компот Fine Life с фейхоа, 1л", "Компот", "180", "finelife.jpg", 10),
                Goods(34, "Компот Fine Life с вишней, 1л", "Компот", "180", "finelifevish.jpg", 10),
                Goods(35, "Компот RIOBA клубника, 1л", "Компот", "160", "riobakompotklub.jpg", 10),
                Goods(36, "Компот ФрутоНяня Вишня и малина, 500мл", "Компот", "110", "frutonanacompotvish.jpg", 10),
                Goods(37, "Арахис Караван орехов жареный соленый, 90г", "Орехи", "87", "caravannuts.jpg", 10),
                Goods(38, "Орех грецкий Seeberger, 150г", "Орехи", "1120", "seebergernursgrec.jpg", 10),
                Goods(39, "Смесь Seeberger орехи и изюм, 150г", "Орехи", "650", "seebergernurs.jpg", 10),
                Goods(41, "Орехи грецкие aro, 800г", "Орехи", "800", "aronuts.jpg", 10),
                Goods(42, "Семечки Бабкины, 100г", "Семечки", "47", "grandma.jpg", 10),
                Goods(43, "Семечки Джинн жареные, 140г", "Семечки", "112", "jin.jpg", 10),
                Goods(44, "Семечки От Мартина Особенные, 200г", "Семечки", "256", "martinspecial.jpg", 10),
                Goods(45, "Семечки Богучарские очищенные, 75г", "Семечки", "51", "bogucharskie.jpg", 10),
                Goods(46, "Семечки От Мартина суперсоленые, 250г", "Семечки", "224", "martinsalt.jpg", 10),
                Goods(47, "Снэк Сухогруз Кальмар сушеный, 70г", "Сушёные морепродукты", "219", "cargoshipkalmar.jpg", 10),
                Goods(48, "Снэк Сухогруз Филе янтарной рыбки, 70г", "Сушёные морепродукты", "152", "cargoshipfish.jpg", 10),
                Goods(49, "Снэк Сухогруз Кольца кальмара, 70г", "Сушёные морепродукты", "249", "cargoshipkolcakalmar.jpg", 10),
                Goods(50, "Снэк Сухогруз Ставридка, 70г", "Сушёные морепродукты", "185", "cargoshipstav.jpg", 10),
                Goods(51, "Сухарики Хрусteam чиабатта пармская ветчина, 55г", "Сухарики", "55", "hrusteamchia.jpg", 10),
                Goods(52, "Сухарики Хрусteam Багет томат и зелень, 60г", "Сухарики", "50", "hrusteamtomat.jpg", 10),
                Goods(53, "Сухарики Хрусteam Сливочный лосось, 40г", "Сухарики", "25", "hrusteamlosos.jpg", 10),
                Goods(54, "Сухарики Хрусteam багет сметана-зелень, 100г", "Сухарики", "84", "hrusteamsmet.jpg", 10)
            )

            val favorite = FavoriteGoods(1, 1, 1)
            val tag = GoodsTags(1, 1, "Снэк")
            val order = Order(1, 1, "ул. Пушкина, д. 10", 1, "2025-06-08")
            val ordered = OrderedGoods(1, 1, 2, 3)
            val cartItem = CartItem(1, 1, 3, 2)

            Log.d("Prepopulate", "Добавление клиента...")
            clientsDao.insertClient(client)

            Log.d("Prepopulate", "Добавление водителя доставки...")
            deliveryDao.insert(driver)

            Log.d("Prepopulate", "Добавление товаров...")
            goodsList.forEach { good ->
                goodsDao.insert(good)
                Log.d("Prepopulate", "Добавлен товар: ${good.name}")
            }

            Log.d("Prepopulate", "Добавление избранного...")
            favoriteDao.insert(favorite)

            Log.d("Prepopulate", "Добавление тега...")
            tagsDao.insert(tag)

            Log.d("Prepopulate", "Добавление заказа...")
            ordersDao.insert(order)

            Log.d("Prepopulate", "Добавление заказа-товара...")
            orderedGoodsDao.insert(ordered)

            Log.d("Prepopulate", "Добавление товара в корзину...")
            cartDao.addToCart(cartItem)

            Log.d("Prepopulate", "Предзаполнение завершено.")
        }
        }
    }
