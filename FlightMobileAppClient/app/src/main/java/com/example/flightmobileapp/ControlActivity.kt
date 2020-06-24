package com.example.flightmobileapp

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.flightmobileapp.connectionView.ServerUrlViewModel.Companion.bitmapScreenShot
import com.google.gson.GsonBuilder
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import io.github.controlwear.virtual.joystick.android.JoystickView
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
import kotlin.math.cos
import kotlin.math.sin


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

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;

        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_screen)
        /*if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){

        }else if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE){

        }*/
        url = intent.getStringExtra("Url")
        val image = findViewById<ImageView>(R.id.imageView)
        image.setImageBitmap(bitmapScreenShot)
        val joystick = findViewById<JoystickView>(R.id.joystickView)
        findViewById<TextView>(R.id.aileronValue).text = "0"
        findViewById<TextView>(R.id.elevatorValue).text = "0"
        findViewById<TextView>(R.id.throttleValue).text = "0"
        findViewById<TextView>(R.id.rudderValue).text = "0"
        sendCommandFromJoystick(joystick)
        sendCommandFromSBThrottle()
        sendCommandFromSBRudder()

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

        val throttleSeekBar = findViewById<VerticalSeekBar>(R.id.seekBarThrottle)
        throttleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener  {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                lastThrottle = progress.toDouble() / 100
                if(lastThrottle==0.0){
                    findViewById<TextView>(R.id.throttleValue).text = "0"
                }
                else {
                    val roundThrottle: Double = String.format("%.2f", lastThrottle).toDouble()
                    findViewById<TextView>(R.id.throttleValue).text = roundThrottle.toString()
                }

                val throttleChange = abs(lastThrottle - currThrottle)
                if(throttleChange > 0.01){
                    currThrottle = lastThrottle
                    CoroutineScope(IO).launch {
                        sendCommand()
                    }
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })

    }

    private fun sendCommandFromSBRudder(){
        val rudderSeekBar = findViewById<SeekBar>(R.id.seekBarRudder)
        rudderSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener  {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                lastRudder = (progress.toDouble() / 100) - 1
                if(lastRudder==0.0){
                    findViewById<TextView>(R.id.rudderValue).text = "0"
                }
                else {
                    val roundRudder: Double = String.format("%.2f", lastRudder).toDouble()
                    findViewById<TextView>(R.id.rudderValue).text = roundRudder.toString()
                }

                val rudderChange = abs(lastRudder - currRudder)
                if(rudderChange > 0.02){
                    currRudder = lastRudder
                    CoroutineScope(IO).launch {
                        sendCommand()
                    }
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Do something
            }
        })
    }

    private fun sendCommandFromJoystick(joystick: JoystickView){
        var isValueChanged = false
        joystick.setOnMoveListener { angle, strength ->

            lastElevator = sin(toRadians(angle.toDouble())) * (strength.toDouble() / 100)
            lastAileron = cos(toRadians(angle.toDouble())) * (strength.toDouble() / 100)
            if(lastAileron==0.0){
                findViewById<TextView>(R.id.aileronValue).text = "0"
            }
            else {
                val roundAileron: Double = String.format("%.2f", lastAileron).toDouble()
                findViewById<TextView>(R.id.aileronValue).text = roundAileron.toString()
            }

            if(lastElevator==0.0){
                findViewById<TextView>(R.id.elevatorValue).text = "0"
            }
            else {
                val roundElevator: Double = String.format("%.2f", lastElevator).toDouble()
                findViewById<TextView>(R.id.elevatorValue).text = roundElevator.toString()
            }

            val elevetorChange = abs(lastElevator - currElevator)
            val aileronChange = abs(lastAileron - currAileron)
            if(elevetorChange > 0.02){
                currElevator = lastElevator;
                isValueChanged = true
            }
            if(aileronChange > 0.02){
                currAileron = lastAileron
                isValueChanged = true
            }

            if(isValueChanged){
                CoroutineScope(IO).launch {
                    sendCommand()
                }
            }
        }

    }

    private fun screenShotThread() {
        isImageRequested = true;
        CoroutineScope(IO).launch {
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
                    val image = findViewById<ImageView>(R.id.imageView)
                    image?.setImageBitmap(imageBm)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}