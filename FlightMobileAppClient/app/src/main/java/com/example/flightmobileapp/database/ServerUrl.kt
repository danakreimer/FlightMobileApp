package com.example.flightmobileapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "url_data_table")
data class ServerUrl (
    @PrimaryKey(autoGenerate = true)
    // Properties
    @ColumnInfo(name = "server_id")
    val id: Int,
    @ColumnInfo(name = "server_url")
    val url: String
)