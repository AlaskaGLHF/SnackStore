package com.example.snackstore.ViewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.snackstore.SnackStoreDatabase
import com.example.snackstore.entity.OrderedGoods
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SnackStoreDatabase.getDatabase(application)
    internal val cartDao = db.cartDao()
    private val orderedDao = db.orderedGoodsDao()
    private val goodsDao = db.goodsDao()

    private val prefs = application.getSharedPreferences("SnackStorePrefs", Context.MODE_PRIVATE)
    val clientId = prefs.getLong("client_id", -1L)

    //val cartItems: Flow<List<CartItem>> = cartDao.getCartItems(clientId)

    /*suspend fun addToCart(goodId: Int) {
        val item = CartItem(client_id = clientId.toInt(), good_id = goodId, quantity = 1)
        cartDao.insert(item)
    }

     */

    suspend fun confirmOrder() {
        val items = cartDao.getCartItems(clientId).first()
        val orderId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        for (item in items) {
            val good = goodsDao.getGoodById(item.good_id)
            val price = good?.price?.toIntOrNull() ?: 0

            repeat(item.quantity) {
                orderedDao.insert(
                    OrderedGoods(
                        order_id = orderId,
                        good_id = item.good_id,
                        full_price = price,
                        date = date
                    )
                )
            }
        }

        cartDao.clearCart(clientId)
    }

    //fun getAllOrders(): Flow<List<Int>> = orderedDao.getAllOrders()
}

