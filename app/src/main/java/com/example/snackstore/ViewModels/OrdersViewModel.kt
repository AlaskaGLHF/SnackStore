package com.example.snackstore.ViewModels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.snackstore.SnackStoreDatabase
import com.example.snackstore.entity.GoodsInfo
import com.example.snackstore.entity.OrderWithGoodsInfo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class OrdersViewModel(application: Application) : AndroidViewModel(application) {
    private val db = SnackStoreDatabase.getDatabase(application)
    private val ordersDao = db.ordersDao()

    private val prefs = application.getSharedPreferences("SnackStorePrefs", Context.MODE_PRIVATE)
    private val clientId = prefs.getLong("client_id", -1L).toInt()

    val ordersWithGoods: StateFlow<List<OrderWithGoodsInfo>> = ordersDao.getOrdersWithGoods(clientId)
        .map { orders ->
            orders.map { orderWithGoods ->
                val totalPrice = orderWithGoods.orderedGoods.sumOf { it.orderedGood.full_price }
                val goodsInfo = orderWithGoods.orderedGoods.map {
                    GoodsInfo(
                        goodId = it.good.id,
                        name = it.good.name ?: "Товар",
                        imagePath = it.good.image_path ?: "", // <= здесь image_path
                        price = it.good.price?.toIntOrNull() ?: 0
                    )
                }
                OrderWithGoodsInfo(
                    orderId = orderWithGoods.order.id,
                    date = orderWithGoods.order.date,
                    totalPrice = totalPrice,
                    goods = goodsInfo
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

}

