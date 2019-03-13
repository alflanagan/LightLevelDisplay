package com.alloydflanagan.mobile.lightleveldisplay

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager

    private var lightSensor: Sensor? = null

    private var lastReading: Float = 0.0f

    private var displayFormat = DecimalFormat("#,##0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (lightSensor == null) {
            tvLevelDisplay.visibility = View.INVISIBLE
            tvMainlabel.setText(R.string.error_no_sensor)
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // don't actually care
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            val newReading = event.values[0]
            if (newReading != lastReading) {
                lastReading = newReading
                val formatted = displayFormat.format(lastReading)
                tvLevelDisplay.text = getString(R.string.value_display, formatted)

                Timber.d("new light level: $formatted lux")
            }
        }
    }
}
