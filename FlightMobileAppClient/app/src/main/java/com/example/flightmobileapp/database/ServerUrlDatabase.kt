package com.example.flightmobileapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ServerUrl::class], version = 1)
abstract class ServerUrlDatabase : RoomDatabase() {
    abstract val serverUrlDAO: ServerUrlDAO

    // Create a singleton object in order to use only one instance of the Room Database in the app
    companion object {
        @Volatile
        private var dbInstance: ServerUrlDatabase? = null

        // Get the instance of the database
        fun getInstance(context: Context): ServerUrlDatabase {
            synchronized(this) {
                var instance: ServerUrlDatabase? = dbInstance
                    // If a ServerUrlDatabase is not yet created
                    if (instance == null) {
                        // Build database
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            ServerUrlDatabase::class.java,
                            "server_url_data_database"
                        ).build()
                    }
                return instance
            }
        }
    }
}