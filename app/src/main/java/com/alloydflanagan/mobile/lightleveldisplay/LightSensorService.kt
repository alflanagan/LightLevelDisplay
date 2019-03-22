package com.alloydflanagan.mobile.lightleveldisplay

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import timber.log.Timber
import kotlin.math.abs

class LightSensorService : Service(), SensorEventListener {

    var isRegistered: Boolean = false
        private set

    /**
     * Convenient functional way to do
     *     if (x == null) log_an_error else do_something(x)
     */
    private fun <T> errorNull(any: T?, msg: String, todo: (value: T) -> Unit) =
        if (any == null) Timber.e(msg) else todo(any)

    /**
     * Creates and sends [Intent] to notify [AppWidgetProvider] to notify widgets.
     */
    private fun triggerUpdate() {
        val intent = Intent(applicationContext, LightDisplayAppWidgetProvider::class.java)
        intent.putExtra(READING_KEY, lastReading)
        sendBroadcast(intent)
        Timber.i("sent update message with value $lastReading")
    }

    /**************** Service methods ***********************************/
    /**
     * Called by the system when the service is first created. Set up to listen to sensor.
     */
    override fun onCreate() {
        super.onCreate()
        errorNull(getSystemService(Context.SENSOR_SERVICE) as SensorManager?,
            "Unable to get sensor manager from system")
        {manager ->
            errorNull(manager.getDefaultSensor(Sensor.TYPE_LIGHT),
                "Unable to get reference to light sensor!!")
            {
                manager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
                isRegistered = true
            }
        }
    }


    /**
     * Called by the system to notify a Service that it is no longer used and is being removed. Unregister
     * as sensor listener.
     */
    override fun onDestroy() {
        val mgr = getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        mgr?.unregisterListener(this)
        isRegistered = false
        super.onDestroy()
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * [android.content.Context.startService], providing the arguments it supplied and a
     * unique integer token representing the start request.
     *
     * @param intent The Intent supplied to [android.content.Context.startService],
     * as given.  This may be null.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to
     * start.  Use with [.stopSelfResult].
     *
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    /**
     * Clients don't need to bind to this service. We send Intents and they receive them.
     */
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**************** SensorEventListener methods ***********************/

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // do nothing
    }

    /**
     * If light value has changed, update `lastReading` and trigger an update event
     */
    override fun onSensorChanged(event: SensorEvent?) {
        errorNull(event?.values, "onSensorChanged called without sensor reading")
        { values ->
            val newLux = values[0]
            if (newLux > 0.0f && abs(newLux - lastReading) > 0.01f) {
                lastReading = newLux
                triggerUpdate()
            }
        }
    }

    /********************************************************************/

    companion object {
        // we have to remember previous reading, since we only update on changes.
        var lastReading: Float = 723.0f
        const val READING_KEY = "reading"
    }

}
