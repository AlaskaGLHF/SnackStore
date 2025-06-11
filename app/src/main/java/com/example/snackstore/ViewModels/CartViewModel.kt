package com.example.snackstore.ViewModels

import android.app.Application
import android.content.Context
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
        return prefs.getLong("client_id", -1L).toInt()
    }

    private val clientId: Int
        get() = getClientIdFromPrefs()

    val cartItems: StateFlow<List<CartItemWithGood>> =
        cartDao.getCartItemsWithGoods(clientId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _orderCreated = MutableSharedFlow<Long>()
    val orderCreated = _orderCreated.asSharedFlow()

    fun addToCart(good: Goods) {
        if (clientId == -1) return
        viewModelScope.launch {
            val items = cartDao.getCartItemsForClient(clientId).first()
            val existing = items.find { it.good_id == good.id }
            if (existing != null) {
                cartDao.updateQuantity(existing.id, existing.quantity + 1)
            } else {
                cartDao.addToCart(CartItem(0, clientId, good.id, 1))
            }
        }
    }

    fun confirmOrder(address: String) {
        viewModelScope.launch {
            val items = cartDao.getCartItemsForClient(clientId).first()
            if (items.isEmpty()) return@launch

            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val order = Order(
                id = 0,
                client_id = clientId,
                address = address,
                delivery_driver_id = 1,
                date = date
            )

            val orderId = ordersDao.insert(order)

            val orderedGoodsList = items.mapNotNull { item ->
                val good = goodsDao.getGoodById(item.good_id) ?: return@mapNotNull null
                val price = good.price?.toIntOrNull() ?: 0
                OrderedGoods(
                    order_id = orderId.toInt(),
                    good_id = item.good_id,
                    full_price = price * item.quantity,
                    date = date
                )
            }

            orderedGoodsList.forEach { orderedGood ->
                orderedDao.insert(orderedGood)
            }

            cartDao.clearCartForClient(clientId)

            _orderCreated.emit(orderId)
        }
    }

    fun removeFromCart(goodId: Int) {
        viewModelScope.launch {
            cartDao.deleteItemByGoodId(goodId)
        }
    }
}

class CartViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}