package com.example.flightmobileapp
import android.widget.Toast
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightmobileapp.database.ServerUrl
import com.example.flightmobileapp.database.ServerUrlRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ServerUrlViewModel(private val repository: ServerUrlRepository) :
    ViewModel(),
    Observable {
    val urls = repository.urls

    @Bindable
    val inputUrl = MutableLiveData<String>()

    fun connect() {
        val url: String = inputUrl.value!!

        // Insert only non-empty URL
        if (url != "") {
            insert(ServerUrl(0, url))
            inputUrl.value = null
        }
    }

    private fun insert(url: ServerUrl): Job = viewModelScope.launch {
        repository.insert(url)
    }

    fun update(url: ServerUrl): Job = viewModelScope.launch {
        repository.update(url)
    }

    fun delete(url: ServerUrl): Job = viewModelScope.launch {
        repository.delete(url)
    }

    fun itemClicked(serverUrl: ServerUrl) {
        inputUrl.value = serverUrl.url
    }

    // Implement Observable Interface
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        // No implementation
    }

    // Implement Observable Interface
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        // No implementation
    }

}