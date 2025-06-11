package com.example.snackstore.ViewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snackstore.SnackStoreDatabase
import com.example.snackstore.entity.FavoriteGoods
import com.example.snackstore.entity.Goods
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoodsViewModel(application: Application) : AndroidViewModel(application) {

    private val goodsDao = SnackStoreDatabase.getDatabase(application).goodsDao()
    private val favoriteDao = SnackStoreDatabase.getDatabase(application).favoriteGoodsDao()
    private val goodsTagsDao = SnackStoreDatabase.getDatabase(application).goodsTagsDao()
    private val sharedPrefs = application.getSharedPreferences("SnackStorePrefs", Context.MODE_PRIVATE)
    private val clientId = sharedPrefs.getLong("client_id", -1L).toInt()

    val goodsList: StateFlow<List<Goods>> = goodsDao.getAllGoods()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteGoodsList: StateFlow<List<Goods>> =
        if (clientId != -1) {
            goodsDao.getFavoriteGoodsWithInfo(clientId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        } else {
            MutableStateFlow(emptyList())
        }

    fun toggleFavorite(good: Goods) {
        if (clientId == -1) return

        viewModelScope.launch {
            val existing = favoriteDao.getFavoriteByClientAndGoods(clientId, good.id)
            if (existing != null) {
                favoriteDao.deleteByClientAndGoods(clientId, good.id)
                Log.d("Prepopulate", "Удалено из избранного: ${good.name}")
            } else {
                favoriteDao.insert(FavoriteGoods(0, clientId, good.id))
                Log.d("Prepopulate", "Добавлено в избранное: ${good.name}")
            }
        }
    }
    private val _searchResults = MutableStateFlow<List<Goods>>(emptyList())
    val searchResults: StateFlow<List<Goods>> = _searchResults

    fun searchGoods(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                _searchResults.value = emptyList()
            } else {
                val result = goodsDao.searchByName("%$query%")
                _searchResults.value = result
            }
        }
    }

    val favoriteGoodsIds: StateFlow<Set<Int>> = favoriteGoodsList
        .map { list -> list.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val allTags: StateFlow<List<String>> = goodsTagsDao.getAllTags()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _selectedTag = MutableStateFlow<String?>(null)

    suspend fun getGoodById(id: Int): Goods? = goodsDao.getGoodById(id)

    fun selectTag(tag: String) {
        Log.d("Prepopulate", "Selected tag: $tag")
        _selectedTag.value = tag
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val goodsByTag: StateFlow<List<Goods>> = _selectedTag
        .filterNotNull()
        .flatMapLatest { tag ->
            Log.d("Prepopulate", "Loading goods by tag: $tag")
            goodsTagsDao.getGoodsByTag(tag)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
}

class GoodsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GoodsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GoodsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
