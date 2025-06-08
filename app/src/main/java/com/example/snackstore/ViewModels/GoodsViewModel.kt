package com.example.snackstore.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snackstore.DAO.GoodsDao
import com.example.snackstore.SnackStoreDatabase
import com.example.snackstore.entity.Goods
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class GoodsViewModel(
    private val goodsDao: GoodsDao
) : ViewModel() {

    // Все товары из базы в виде StateFlow
    val goodsList: StateFlow<List<Goods>> = goodsDao.getAllGoods()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Если нужно, можно добавить функции для вставки, удаления и т.д.
}

class GoodsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoodsViewModel::class.java)) {
            val dao = SnackStoreDatabase.getDatabase(context).goodsDao()
            @Suppress("UNCHECKED_CAST")
            return GoodsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}