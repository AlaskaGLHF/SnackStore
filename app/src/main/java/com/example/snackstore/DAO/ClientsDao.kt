package com.example.snackstore.DAO


import com.example.snackstore.entity.Client
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientsDao {

    @Query("SELECT * FROM Clients")
    suspend fun getAllClients(): List<Client>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client)

    @Delete
    suspend fun deleteClient(client: Client)

    @Query("SELECT * FROM Clients WHERE email = :email AND password = :password LIMIT 1")
    suspend fun getClientByEmailAndPassword(email: String, password: String): Client?

    @Query("SELECT * FROM clients WHERE id = :clientId LIMIT 1")
    fun getClientById(clientId: Long): Flow<Client?>

}
