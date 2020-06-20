package com.example.flightmobileapp.connectionView
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightmobileapp.ControlActivity
import com.example.flightmobileapp.database.ServerUrl
import com.example.flightmobileapp.database.ServerUrlRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*


class ServerUrlViewModel(private val repository: ServerUrlRepository,
                         private val applicationContext: Context) :
    ViewModel(),
    Observable {
    // Five most recently used urls from data table
    val urls = repository.urls

    @Bindable
    // User input
    val inputUrl = MutableLiveData<String>()

    @RequiresApi(Build.VERSION_CODES.O)
    // Insert url to database, connect to server and start control activity
    fun connect() {
        val url: String? = inputUrl.value
        // If URL is empty
        if (url.isNullOrBlank()) {
            // Show an error message
            Toast.makeText(applicationContext, "Please enter URL", Toast.LENGTH_SHORT).show()
        // Insert only non-empty URL that contains no spaces
        } else {


            /*

            Send a request to get a screenshot from the simulator

             */


            // If connection succeeded
            if (false) {
                // Start control activity
                startControlActivity(url)
            // If connection failed
            } else {
                // Show an error message
                Toast.makeText(applicationContext, "Connection failed. Please try again",
                    Toast.LENGTH_SHORT).show()
            }
            // Insert URL to database
            insert(ServerUrl(url.toLowerCase(Locale.ROOT), LocalDateTime.now().toString()))
            inputUrl.value = null
        }
    }

    // Start control activity
    private fun startControlActivity(url: String) {
        val intent = Intent(applicationContext, ControlActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Pass URL to the control activity
        intent.putExtra("Url", url)

        /*

        ----- To get the URL from the control activity, use: -----
        val url = intent.getStringExtra("Url")

         */

        applicationContext.startActivity(intent)
    }

    // Insert a ServerUrl object to data table
    private fun insert(url: ServerUrl): Job = viewModelScope.launch {
        val newRowId: Long = repository.insert(url)
        // If insertion failed
        if (newRowId <= -1) {
            // Show an error message
            Toast.makeText(applicationContext,"URL insertion failed",Toast.LENGTH_SHORT).show()
        }
    }

    // Change the URL text view to display the clicked URL
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