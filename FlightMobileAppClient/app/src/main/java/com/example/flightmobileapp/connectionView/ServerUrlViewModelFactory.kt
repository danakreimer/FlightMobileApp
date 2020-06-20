package com.example.flightmobileapp.connectionView
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flightmobileapp.database.ServerUrlRepository
import java.lang.IllegalArgumentException

class ServerUrlViewModelFactory(private val repository: ServerUrlRepository,
                                private val applicationContext: Context) :
    ViewModelProvider.Factory {
    // Create ServerUrl View Model
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServerUrlViewModel::class.java)) {
            return ServerUrlViewModel(
                repository,
                applicationContext
            ) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}