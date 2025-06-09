package com.example.snackstore.ViewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snackstore.SnackStoreDatabase
import com.example.snackstore.entity.CartItem
import com.example.snackstore.entity.CartItemWithGood
import com.example.snackstore.entity.Goods
import com.example.snackstore.entity.Order
import com.example.snackstore.entity.OrderedGoods
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SnackStoreDatabase.getDatabase(application)
    private val cartDao = db.cartDao()
    private val orderedDao = db.orderedGoodsDao()
    private val ordersDao = db.ordersDao()
    private val goodsDao = db.goodsDao()

    private fun getClientIdFromPrefs(): Int {
        val prefs = getApplication<Application>().getSharedPreferences("SnackStorePrefs", Context.MODE_PRIVATE)
        val id = prefs.getLong("client_id", -1L).toInt()
        Log.d("Prepopulate", "Loaded clientId from prefs: $id")
        return id
    }


    private val clientId: Int
        get() = getClientIdFromPrefs()

    val cartItems: StateFlow<List<CartItemWithGood>> =
        cartDao.getCartItemsWithGoods(clientId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addToCart(good: Goods) {
        val clientId = getClientIdFromPrefs()
        Log.d("Prepopulate", "addToCart called for ${good.name}, clientId = $clientId")
        if (clientId == -1) {
            Log.e("Prepopulate", "Invalid clientId, cannot add to cart")
            return
        }
        viewModelScope.launch {
            val items = cartDao.getCartItemsForClient(clientId).first()
            val existing = items.find { it.good_id == good.id }
            if (existing != null) {
                cartDao.updateQuantity(existing.id, existing.quantity + 1)
            } else {
                cartDao.addToCart(CartItem(0, clientId, good.id, 1))
            }
            Log.d("Prepopulate", "Товар добавлен в корзину: ${good.name}")
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartDao.clearCartForClient(clientId)
        }
    }

    fun confirmOrder() {
        viewModelScope.launch {
            val items = cartDao.getCartItemsForClient(clientId).first()
            if (items.isEmpty()) return@launch

            val orderId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val order = Order(
                id = orderId,
                client_id = clientId,
                address = "Молдавская дом 5",
                delivery_driver_id = 1,
                date = date
            )
            ordersDao.insert(order)

            items.forEach { item ->
                val good = goodsDao.getGoodById(item.good_id)
                val price = good?.price?.toIntOrNull() ?: 0
                orderedDao.insert(
                    OrderedGoods(
                        order_id = orderId,
                        good_id = item.good_id,
                        full_price = price * item.quantity,
                        date = date
                    )
                )
            }
            cartDao.clearCartForClient(clientId)
        }
        clearCart()
    }
}

// Factory для создания CartViewModel с передачей Application
class CartViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
