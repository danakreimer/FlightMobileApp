package com.example.flightmobileapp.database

// A Repository manages queries and allows to use multiple backend
class ServerUrlRepository(private val dao: ServerUrlDAO) {
    // Five most recently used urls from data table.
    // Observed LiveData will notify the observer when the data has changed
    val urls = dao.getMostRecentlyUsedUrls()

    // Insert a ServerUrl object to data table and return its row ID
    suspend fun insert(serverUrl: ServerUrl): Long {
        return dao.insertUrl(serverUrl)
     }
}