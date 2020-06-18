package com.example.flightmobileapp.database

class ServerUrlRepository(private val dao: ServerUrlDAO) {
    // Five most recently used urls from data table
    val urls = dao.getMostRecentlyUsedUrls()

    // Insert a ServerUrl object to data table
    suspend fun insert(url: ServerUrl) {
         dao.insertUrl(url)
     }

    // Update a ServerUrl object in data table
    suspend fun update(url: ServerUrl) {
        dao.updateUrl(url)
    }

    // Delete a ServerUrl object from data table
    suspend fun delete(url: ServerUrl) {
        dao.deleteUrl(url)
    }
}