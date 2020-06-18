package com.example.flightmobileapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.flightmobileapp.database.ServerUrlRepository
import java.lang.IllegalArgumentException

class ServerUrlViewModelFactory(private val repository: ServerUrlRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServerUrlViewModel::class.java)) {
            return ServerUrlViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }
}