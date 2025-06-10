package com.example.snackstore.ViewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snackstore.SnackStoreDatabase
import com.example.snackstore.entity.GoodsInfo
import com.example.snackstore.entity.OrderWithGoodsInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrdersViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SnackStoreDatabase.getDatabase(application)

    private val ordersDao = db.ordersDao()
    private val orderedGoodsDao = db.orderedGoodsDao()
    private val goodsDao = db.goodsDao()

    private val prefs = application.getSharedPreferences("SnackStorePrefs", Context.MODE_PRIVATE)
    private val clientId = prefs.getLong("client_id", -1L).toInt()

    private val _ordersWithGoods = MutableStateFlow<List<OrderWithGoodsInfo>>(emptyList())
    val ordersWithGoods: StateFlow<List<OrderWithGoodsInfo>> = _ordersWithGoods

    init {
        viewModelScope.launch {
            loadOrders()
        }
    }

    fun reloadOrders() {
        viewModelScope.launch {
            loadOrders()
        }
    }

    private suspend fun loadOrders() {
        withContext(Dispatchers.IO) {
            val orders = ordersDao.getOrdersByClient(clientId)
            Log.d("Prepopulate", "Загружено заказов: ${orders.size}")

            val result = orders.map { order ->
                val orderedGoods = orderedGoodsDao.getOrderedGoodsByOrder(order.id)
                Log.d("Prepopulate", "Заказ ${order.id} содержит ${orderedGoods.size} позиций товаров")

                val goodsInfo = orderedGoods.mapNotNull { og ->
                    val good = goodsDao.getGoodById(og.good_id)
                    Log.d("Prepopulate", "Товар в заказе ${order.id}: good_id = ${og.good_id}, имя = ${good?.name ?: "НЕ НАЙДЕН"}")
                    good?.let {
                        GoodsInfo(
                            goodId = it.id,
                            name = it.name ?: "Товар",
                            imagePath = it.image_path ?: "",
                            price = it.price?.toIntOrNull() ?: 0
                        )
                    }
                }

                val totalPrice = orderedGoods.sumOf { it.full_price }

                OrderWithGoodsInfo(
                    orderId = order.id,
                    date = order.date,
                    totalPrice = totalPrice,
                    goods = goodsInfo
                )
            }
            Log.d("Prepopulate", "Формирование списка заказов с товарами завершено. Всего заказов: ${result.size}")
            _ordersWithGoods.value = result
        }
    }
}

@Suppress("UNCHECKED_CAST")
class OrdersViewModelFactory(private val application: Application) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OrdersViewModel(application) as T
    }
}

