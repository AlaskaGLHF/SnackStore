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
                            Log.d("Prepopulate", "БД создана. Предзаполнение...")
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
            val tagsDao = db.goodsTagsDao()
            val deliveryDao = db.deliveryDriverDao()

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

            val tag = listOf(
                GoodsTags(id = 1, good_id = 1, tag = "Газированный напиток"),
                GoodsTags(id = 2, good_id = 2, tag = "Газированный напиток"),
                GoodsTags(id = 3, good_id = 3, tag = "Газированный напиток"),
                GoodsTags(id = 4, good_id = 4, tag = "Газированный напиток"),
                GoodsTags(id = 5, good_id = 5, tag = "Газированный напиток"),
                GoodsTags(id = 6, good_id = 6, tag = "Газированный напиток"),
                GoodsTags(id = 7, good_id = 7, tag = "Газированный напиток"),

                GoodsTags(id = 8, good_id = 8, tag = "Чипсы"),
                GoodsTags(id = 9, good_id = 9, tag = "Чипсы"),
                GoodsTags(id = 10, good_id = 10, tag = "Чипсы"),
                GoodsTags(id = 11, good_id = 11, tag = "Чипсы"),
                GoodsTags(id = 12, good_id = 12, tag = "Чипсы"),

                GoodsTags(id = 18, good_id = 23, tag = "Сок"),
                GoodsTags(id = 19, good_id = 24, tag = "Сок"),
                GoodsTags(id = 20, good_id = 25, tag = "Сок"),
                GoodsTags(id = 21, good_id = 26, tag = "Сок"),
                GoodsTags(id = 22, good_id = 27, tag = "Сок"),

                GoodsTags(id = 23, good_id = 28, tag = "Морс"),
                GoodsTags(id = 24, good_id = 29, tag = "Морс"),
                GoodsTags(id = 25, good_id = 30, tag = "Морс"),
                GoodsTags(id = 26, good_id = 31, tag = "Морс"),
                GoodsTags(id = 27, good_id = 32, tag = "Морс"),

                GoodsTags(id = 28, good_id = 33, tag = "Компот"),
                GoodsTags(id = 29, good_id = 34, tag = "Компот"),
                GoodsTags(id = 30, good_id = 35, tag = "Компот"),
                GoodsTags(id = 31, good_id = 36, tag = "Компот"),

                GoodsTags(id = 32, good_id = 37, tag = "Орехи"),
                GoodsTags(id = 33, good_id = 38, tag = "Орехи"),
                GoodsTags(id = 34, good_id = 39, tag = "Орехи"),
                GoodsTags(id = 35, good_id = 41, tag = "Орехи"),

                GoodsTags(id = 36, good_id = 42, tag = "Семечки"),
                GoodsTags(id = 37, good_id = 43, tag = "Семечки"),
                GoodsTags(id = 38, good_id = 44, tag = "Семечки"),
                GoodsTags(id = 39, good_id = 45, tag = "Семечки"),
                GoodsTags(id = 40, good_id = 46, tag = "Семечки"),

                GoodsTags(id = 41, good_id = 47, tag = "Сушёные морепродукты"),
                GoodsTags(id = 42, good_id = 48, tag = "Сушёные морепродукты"),
                GoodsTags(id = 43, good_id = 49, tag = "Сушёные морепродукты"),
                GoodsTags(id = 44, good_id = 50, tag = "Сушёные морепродукты"),

                GoodsTags(id = 45, good_id = 51, tag = "Сухарики"),
                GoodsTags(id = 46, good_id = 52, tag = "Сухарики"),
                GoodsTags(id = 47, good_id = 53, tag = "Сухарики"),
                GoodsTags(id = 48, good_id = 54, tag = "Сухарики"),
            )

            Log.d("Prepopulate", "Добавление клиента...")
            clientsDao.insertClient(client)

            Log.d("Prepopulate", "Добавление водителя доставки...")
            deliveryDao.insert(driver)

            Log.d("Prepopulate", "Добавление товаров...")
            goodsList.forEach { good ->
                goodsDao.insert(good)
                Log.d("Prepopulate", "Добавлен товар: ${good.name}")
            }

            Log.d("Prepopulate", "Добавление тега...")
            tagsDao.insertAll(tag)

            Log.d("Prepopulate", "Предзаполнение завершено.")
        }
        }
    }