package com.example.snackstore.ViewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snackstore.SnackStoreDatabase
import com.example.snackstore.entity.Client
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.core.content.edit

class ClientViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = SnackStoreDatabase.getDatabase(application).clientsDao()

    var authResult by mutableStateOf<Client?>(null)
        private set

    var registrationSuccess by mutableStateOf(false)
        private set

    private val _client = MutableStateFlow<Client?>(null)
    val client: StateFlow<Client?> = _client

    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d("Prepopulate", "Ищем клиента с email=$email и password=$password")
            val client = dao.getClientByEmailAndPassword(email, password)
            Log.d("Prepopulate", "Результат авторизации: $client")
            authResult = client
            client?.let {
                saveClientIdToPrefs(it.id.toLong())
            }
        }
    }

    private fun saveClientIdToPrefs(clientId: Long) {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("SnackStorePrefs", Context.MODE_PRIVATE)
        sharedPrefs.edit { putLong("client_id", clientId) }
        Log.d("Prepopulate", "Client ID сохранён: $clientId")
    }

    fun register(client: Client) {
        viewModelScope.launch {
            dao.insertClient(client)
            registrationSuccess = true
        }
    }

    fun resetState() {
        authResult = null
        registrationSuccess = false
    }

    fun getClientById(clientId: Long) = dao.getClientById(clientId)

    fun loadClient(clientId: Long) {
        Log.d("Prepopulate", "Загружаем клиента с id: $clientId")
        viewModelScope.launch {
            dao.getClientById(clientId).collect { clientData ->
                _client.value = clientData
            }
        }
    }

}

class ClientViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClientViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
