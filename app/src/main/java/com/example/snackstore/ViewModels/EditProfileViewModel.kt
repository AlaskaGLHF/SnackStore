package com.example.snackstore.ViewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.snackstore.SnackStoreDatabase
import com.example.snackstore.entity.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val clientDao = SnackStoreDatabase.getDatabase(application).clientsDao()


    private val _client = MutableStateFlow<Client?>(null)
    val client: StateFlow<Client?> = _client


    init {
        val sharedPreferences = application.getSharedPreferences("SnackStorePrefs", Context.MODE_PRIVATE)
        val clientId = sharedPreferences.getLong("client_id", -1L)
        Log.d("Prepopulate", "SharedPreferences client_id: $clientId")

        if (clientId != -1L) {
            viewModelScope.launch {
                clientDao.getClientById(clientId).collect { loadedClient ->
                    Log.d("Prepopulate", "Загружен клиент: $loadedClient")
                    _client.value = loadedClient
                }
            }
        } else {
            Log.w("Prepopulate", "clientId не найден в SharedPreferences")
        }
    }

    fun updateClient(client: Client) {
        viewModelScope.launch {
            clientDao.updateClient(client)
            _client.value = client  // обновим текущего
            Log.d("Prepopulate", "Клиент обновлён: $client")
        }
    }

    /*private fun getClientIdFromPrefs(context: Context): Long {
        val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)
        return prefs.getLong("clientId", -1L)
    }

     */
}
