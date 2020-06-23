package com.example.flightmobileapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.flightmobileapp.JoystickView.OnMoveListener
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.toRadians
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.cos


class ControlActivity : AppCompatActivity() {
    private var currAileron: Double = 0.0
    private var currThrottle: Double = 0.0
    private var currRudder: Double = 0.0
    private var currElevator: Double = 0.0

    private var lastAileron: Double = 0.0
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
        sendCommandFromJoystick(joystick)


    }



    //visible
    override fun onStart() {
        super.onStart()
        //screenShotThread()

    }

    // no longer visible
    override fun onStop() {
        super.onStop()
        isImageRequested = false
    }

    // returns to this activity
    override fun onResume() {
        super.onResume()
        //screenShotThread()
    }

    override fun onPause() {
        super.onPause()
        isImageRequested = false
    }


    fun sendCommand() {
        val jsonToSend: String = "{\"aileron\": $currAileron,\n \"rudder\": $currRudder, \n " +
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
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            }
        })
    }


    private fun sendCommandFromSBThrottle(){

    }

    private fun sendCommandFromSBRudder(){

    }

    private fun sendCommandFromJoystick(joystick: JoystickView){
        var isValueChanged = false
        joystick.setOnMoveListener (object : OnMoveListener {
            override fun onMove(angle: Int, strength: Int) {

                lastElevator = sin(toRadians(angle.toDouble())) * (strength.toDouble() / 100)
                lastAileron = cos(toRadians(angle.toDouble())) * (strength.toDouble() / 100)
                val elevetorChange = abs(lastElevator - currElevator)
                val aileronChange = abs(lastAileron - currAileron)
                if(elevetorChange > 0.1){
                    currElevator = lastElevator;
                    isValueChanged = true
                }
                if(aileronChange > 0.1){
                    currAileron = lastAileron
                    isValueChanged = true
                }

                if(isValueChanged){
                    requestScope.launch {
                        sendCommand()
                    }
                }

            }
        })
    }

    private fun screenShotThread() {
        isImageRequested = true;
        requestScope.launch {
            while(isImageRequested){
                getScreenShot()
                delay(250)
            }
        }
    }

    private fun getScreenShot() {
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