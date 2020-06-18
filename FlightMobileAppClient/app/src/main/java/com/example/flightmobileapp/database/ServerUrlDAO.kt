package com.example.flightmobileapp.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ServerUrlDAO {
    @Insert
    // Insert a ServerUrl object to data table
    suspend fun insertUrl(url: ServerUrl)

    @Update
    // Update a ServerUrl object in data table
    suspend fun updateUrl(url: ServerUrl)

    @Delete
    // Delete a ServerUrl object from data table
    suspend fun deleteUrl(url: ServerUrl)

    @Query("SELECT server_url, server_id FROM url_data_table ORDER BY server_id DESC limit 5")
    // Get the most recently used urls from data table
    fun getMostRecentlyUsedUrls(): LiveData<List<ServerUrl>>
}