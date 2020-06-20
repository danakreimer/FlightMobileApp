package com.example.flightmobileapp.database
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
// In the DAO (data access object), SQL queries are specified and associated with method calls
interface ServerUrlDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Insert a ServerUrl object to data table and return its row ID
    suspend fun insertUrl(url: ServerUrl): Long

    @Query("SELECT server_url, last_use FROM url_data_table ORDER BY last_use DESC limit 5")
    // Get the most recently used urls from data table
    fun getMostRecentlyUsedUrls(): LiveData<List<ServerUrl>>
}