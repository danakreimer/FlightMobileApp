package com.example.flightmobileapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flightmobileapp.connectionView.ServerUrlViewModel.Companion.bitmapScreenShot
import com.google.gson.GsonBuilder
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Math.toRadians
import java.util.concurrent.TimeUnit
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
    private var shouldFetchImage = false;
    private var url: String? = null
    private var image: ImageView? = null
    private var api: Api? = null

    // Initialize the variables and execute the command and the screenshot functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bind the activity with the control screen view
        setContentView(R.layout.control_screen)
        // Get the Url from the connect activity
        url = intent.getStringExtra("Url")
        // Send the first screenshot request
        firstScreenShotRequest()
        // Get the joystick from the control screen view
        val joystick = findViewById<JoystickView>(R.id.joystickView)
        // Initialize the text of the four values to "0"
        findViewById<TextView>(R.id.aileronValue).text = "0"
        findViewById<TextView>(R.id.elevatorValue).text = "0"
        findViewById<TextView>(R.id.throttleValue).text = "0"
        findViewById<TextView>(R.id.rudderValue).text = "0"
        // Execute the functions of the screenshot, the joystick and the seek bars
        sendCommandFromJoystick(joystick)
        sendCommandFromSBThrottle()
        sendCommandFromSBRudder()
        screenShotThread()
    }

    // Send the first request of getting a screenshot
    private fun firstScreenShotRequest() {
        val dispatcher: Dispatcher = Dispatcher()
        dispatcher.maxRequests = 1
        val okHttpClient = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl(url.toString())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        api = retrofit.create(Api::class.java)
        image = findViewById<ImageView>(R.id.imageView)
        image?.setImageBitmap(bitmapScreenShot)
    }

    // Save the values of the fields when the screen rotates
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble("cAileron", currAileron)
        outState.putDouble("cElevator", currElevator)
        outState.putDouble("cThrottle", currThrottle)
        outState.putDouble("cRudder", currRudder)
        outState.putDouble("lAileron", lastAileron)
        outState.putDouble("lElevator", lastElevator)
        outState.putDouble("lThrottle", lastThrottle)
        outState.putDouble("lRudder", lastRudder)
    }

    // Restore the values after the screen rotated
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currAileron = savedInstanceState.getDouble("cAileron")
        currElevator = savedInstanceState.getDouble("cElevator")
        currThrottle = savedInstanceState.getDouble("cThrottle")
        currRudder = savedInstanceState.getDouble("cElevator")
        lastAileron = savedInstanceState.getDouble("lAileron")
        lastElevator = savedInstanceState.getDouble("lElevator")
        lastThrottle = savedInstanceState.getDouble("lThrottle")
        lastRudder = savedInstanceState.getDouble("lRudder")
    }

    override fun onDestroy() {
        super.onDestroy()
        shouldFetchImage = false
    }

    // Execute when the activity is visible
    override fun onStart() {
        super.onStart()
        shouldFetchImage = true;
    }

    // Execute when the activity is no longer visible
    override fun onStop() {
        super.onStop()
        shouldFetchImage = false
    }

    // Execute when the user returns to this activity
    override fun onResume() {
        super.onResume()
        shouldFetchImage = true;
    }

    // Execute when another activity comes into the foreground
    override fun onPause() {
        super.onPause()
        shouldFetchImage = false
    }

    // Send a post command with the current values
    fun sendCommand() {
        // Creates the json string
        val jsonToSend: String = "{\"aileron\": $currAileron,\n \"rudder\": $currRudder, \n " +
                "\"elevator\": $currElevator, \n \"throttle\": $currThrottle\n}"

        val requestBody: RequestBody =
            RequestBody.create(MediaType.parse("application/json"), jsonToSend)

        api?.postCommand(requestBody)?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // Check if the request failed
                if (response.code() != 200) {
                    showErrorMessage("Failed to send command")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showErrorMessage("Connection with server failed")
            }
        })
    }

    // Create a toast error message
    private fun showErrorMessage(messageToAdd: String) {
        Toast.makeText(
            applicationContext,
            "$messageToAdd. Please go back and try to reconnect",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Send a post command when the throttle seek bar changes
    private fun sendCommandFromSBThrottle() {
        val throttleSeekBar = findViewById<VerticalSeekBar>(R.id.seekBarThrottle)
        throttleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                lastThrottle = progress.toDouble() / 100
                if (lastThrottle == 0.0) {
                    findViewById<TextView>(R.id.throttleValue).text = "0"
                } else {
                    lastThrottle = String.format("%.2f", lastThrottle).toDouble()
                    findViewById<TextView>(R.id.throttleValue).text = lastThrottle.toString()
                }
                // Check whether the Throttle value has changed by more than 1% of its range
                if (checkIfPassOnePercent("Throttle", lastThrottle, currThrottle)) {
                    currThrottle = lastThrottle
                    sendCommand()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // Send a post command when the rudder seek bar changes
    private fun sendCommandFromSBRudder() {
        val rudderSeekBar = findViewById<SeekBar>(R.id.seekBarRudder)
        rudderSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {
                lastRudder = (progress.toDouble() / 100) - 1
                if (lastRudder == 0.0) {
                    findViewById<TextView>(R.id.rudderValue).text = "0"
                } else {
                    lastRudder = String.format("%.2f", lastRudder).toDouble()
                    findViewById<TextView>(R.id.rudderValue).text = lastRudder.toString()
                }

                // Check whether the Rudder value has changed by more than 1% of its range
                if (checkIfPassOnePercent("Rudder", lastRudder, currRudder)) {
                    currRudder = lastRudder
                    sendCommand()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // Send a post command when the joystick moves
    private fun sendCommandFromJoystick(joystick: JoystickView) {
        joystick.setOnMoveListener { angle, strength ->
            lastElevator = sin(toRadians(angle.toDouble())) * (strength.toDouble() / 100)
            lastAileron = cos(toRadians(angle.toDouble())) * (strength.toDouble() / 100)
            if (lastAileron == 0.0) {
                findViewById<TextView>(R.id.aileronValue).text = "0"
            } else {
                lastAileron = String.format("%.2f", lastAileron).toDouble()
                findViewById<TextView>(R.id.aileronValue).text = lastAileron.toString()
            }
            if (lastElevator == 0.0) {
                findViewById<TextView>(R.id.elevatorValue).text = "0"
            } else {
                lastElevator = String.format("%.2f", lastElevator).toDouble()
                findViewById<TextView>(R.id.elevatorValue).text = lastElevator.toString()
            }
            // Check whether the Elevator value has changed by more than 1% of its range
            if (checkIfPassOnePercent("Elevator", lastElevator, currElevator)) {
                currElevator = lastElevator;
                sendCommand()
            }
            // Check whether the Aileron value has changed by more than 1% of its range
            if (checkIfPassOnePercent("Aileron", lastAileron, currAileron)) {
                currAileron = lastAileron
                sendCommand()
            }
        }
    }

    // Check whether the parameter has changed by more than 1% of its range
    private fun checkIfPassOnePercent(paramName: String, lastValue: Double, currValue: Double):
            Boolean {
        val change = abs(lastValue - currValue)
        val onePercent = if (paramName.compareTo("Throttle") == 0) {
            0.01
        } else {
            0.02
        }
        if (change > onePercent) {
            return true
        }
        return false
    }

    // Send the screen shot request every 500 milliseconds
    private fun screenShotThread() {
        CoroutineScope(IO).launch {
            while (!isDestroyed) {
                if (shouldFetchImage) {
                    getScreenShot()
                }

                delay(500)
            }
        }
    }

    // Create screen shot request
    private fun getScreenShot() {
        api?.getImg()?.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val responseBytes = response.body()?.byteStream()
                if ((response.code() != 200) || (responseBytes == null)) {
                    showErrorMessage("Failed to get screenshot")
                } else {
                    val imageBm = BitmapFactory.decodeStream(responseBytes)

                    // Close stream after decoding
                    responseBytes.close()
                    runOnUiThread {
                        image = findViewById(R.id.imageView)
                        image?.setImageBitmap(imageBm)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                showErrorMessage("Connection with server failed")
            }
        })
    }
}