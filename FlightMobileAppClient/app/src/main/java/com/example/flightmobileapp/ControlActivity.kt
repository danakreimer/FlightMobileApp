package com.example.flightmobileapp

import android.os.Bundle
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import com.example.flightmobileapp.JoystickView.OnMoveListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory


class ControlActivity : AppCompatActivity() {
    private var currAilaroen: Double = 0.0
    private var currThrottle: Double = 0.0
    private var currRudder: Double = 0.0
    private var currElevator: Double = 0.0

    private var lastAilaroen: Double = 0.0
    private var lastThrottle: Double = 0.0
    private var lastRudder: Double = 0.0
    private var lastElevator: Double = 0.0

    private var isImageRequested = false;
    private var url: String? = null
    private var image: ImageView? = null
    private val requestScope = CoroutineScope(IO);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_screen)
        url = intent.getStringExtra("Url")
        image = findViewById<ImageView>(R.id.imageView)
        val joystick = findViewById<JoystickView>(R.id.joystickView)
        joystick.setOnMoveListener (object : OnMoveListener {
            override fun onMove(angle: Int, strength: Int) {

            }
        })
            // do whatever you want
    }

    //visible
    override fun onStart() {
        super.onStart()
        /*
        screenShotThread()

         */

    }

    // no longer visible
    override fun onStop() {
        super.onStop()
        isImageRequested = false
    }

    // returns to this activity
    override fun onResume() {
        super.onResume()
        /*
        screenShotThread()
         */
    }

    override fun onPause() {
        super.onPause()
        isImageRequested = false
    }




    fun sendCommand() {
        val jsonToSend: String = "{\"aileron\": $currAilaroen,\n \"rudder\": $currRudder, \n " +
                "\"elevator\": $currElevator, \n \"throttle\": $currThrottle\n}"

        val requestBody: RequestBody =
            RequestBody.create(MediaType.parse("application/json"), jsonToSend)

        val gson = GsonBuilder().setLenient().create()

        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(this.url.toString())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        val api = retrofit.create(Api::class.java)
        val resBody = api.postCommand(requestBody).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                TODO("Not yet implemented")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    fun screenShotThread() {
        isImageRequested = true;
        requestScope.launch {
            while(isImageRequested){
                getScreenShot()
                delay(250)
            }
        }
    }

    fun getScreenShot() {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(this.url.toString())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        val body = api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBytes = response.body()?.byteStream()
                if(responseBytes == null){

                }
                val imageBm = BitmapFactory.decodeStream(responseBytes)
                runOnUiThread {
                    image?.setImageBitmap(imageBm)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}