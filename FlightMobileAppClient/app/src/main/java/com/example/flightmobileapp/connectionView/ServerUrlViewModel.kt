package com.example.flightmobileapp.connectionView
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.webkit.URLUtil
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightmobileapp.Api
import com.example.flightmobileapp.ControlActivity
import com.example.flightmobileapp.database.ServerUrl
import com.example.flightmobileapp.database.ServerUrlRepository
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class ServerUrlViewModel(private val repository: ServerUrlRepository,
                         private val applicationContext: Context) :
    ViewModel(),
    Observable {
    // Five most recently used urls from data table
    val urls = repository.urls

    companion object {
        lateinit var bitmapScreenShot: Bitmap
    }
    
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
        // If URL is not valid
        } else if (!URLUtil.isValidUrl(url)) {
                Toast.makeText(applicationContext, "Invalid URL", Toast.LENGTH_SHORT).show()
        // If URL is valid
        } else {
            // Try to get a screenshot and start control activity
            getScreenshot(url);
            val date = Date()
            val dateString = SimpleDateFormat.getDateTimeInstance().format(date);
            // Insert URL to database
            insert(ServerUrl(url.toLowerCase(Locale.ROOT), dateString))
            inputUrl.value = null
        }
    }

    // Send a request to get a screenshot.
    // If a screenshot receives successfully, start control activity
    private fun getScreenshot(url: String) {
        val gson = GsonBuilder().setLenient().create()
        val okHttpClient: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS).build()
        val retrofit = Retrofit.Builder().baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient).build()
        val api = retrofit.create(Api::class.java)
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>) {
                val responseBytes = response.body()?.byteStream()
                // If getting a screenshot failed
                if ((response.code() != 200) || (responseBytes == null)) {
                    // Show an error message
                    Toast.makeText(applicationContext,
                        "Connection failed. Please try again",
                        Toast.LENGTH_SHORT).show()
                // If getting a screenshot succeeded
                } else {
                    bitmapScreenShot = BitmapFactory.decodeStream(responseBytes)
                    // Start control activity
                    startControlActivity(url)
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Show an error message
                Toast.makeText(applicationContext, "Connection failed. Please try again",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Start control activity
    private fun startControlActivity(url: String) {
        val intent = Intent(applicationContext, ControlActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Pass URL to the control activity
        intent.putExtra("Url", url)
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