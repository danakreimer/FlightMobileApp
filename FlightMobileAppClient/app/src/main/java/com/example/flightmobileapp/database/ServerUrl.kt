package com.example.flightmobileapp.database
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "url_data_table")
data class ServerUrl (
    // Properties
    @PrimaryKey
    @ColumnInfo(name = "server_url")
    val url: String,
    @ColumnInfo(name = "last_use")
    val lastUse: String
)