package com.example.snackstore.ViewModels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.snackstore.SnackStoreDatabase
import com.example.snackstore.entity.Client
import kotlinx.coroutines.launch

class ClientViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = SnackStoreDatabase.getDatabase(application).clientsDao()

    var authResult by mutableStateOf<Client?>(null)
        private set

    var registrationSuccess by mutableStateOf(false)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authResult = dao.getClientByEmailAndPassword(email, password)
        }
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
