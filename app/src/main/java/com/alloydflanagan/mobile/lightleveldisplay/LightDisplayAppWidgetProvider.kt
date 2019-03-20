package com.alloydflanagan.mobile.lightleveldisplay

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.RemoteViews
import timber.log.Timber
import java.text.DecimalFormat
import kotlin.math.abs

class LightDisplayAppWidgetProvider: LoggedAppWidgetProvider(), SensorEventListener {
    /// since sensor callback doesn't get a context, we have to keep a ref to ours
    private var myContext: Context? = null

    /**
     * Convenient functional way to do
     *     if (x == null) log_an_error else do_something(x)
     */
    private fun <T> errorNull(any: T?, msg: String, todo: (value: T) -> Unit) =
        if (any == null) Timber.e(msg) else todo(any)

    /**
     * log error if any of three values are null, else call provided function with 3 arguments
     */
    private fun <T, U, V> errorNull(any: T?, any2: U?, any3: V?, msg: String, todo: (value: T, value2: U, value3: V) -> Unit) =
        if (any == null || any2 == null || any3 == null) Timber.e(msg) else todo(any, any2, any3)

    // this occurs when the widget is placed
    override fun onEnabled(context: Context?) {
         // Maybe: create a special purpose activity to act as sensor listener, callback
        errorNull(context, "Wow, enabled without a context. That'a ... awkward.")
        {
            errorNull(it.getSystemService(Context.SENSOR_SERVICE) as SensorManager?,
                "Unable to get sensor manager from system")
            {manager ->
                  errorNull(manager.getDefaultSensor(Sensor.TYPE_LIGHT),
                      "Unable to get reference to light sensor!!")
                  {
                      manager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
                  }
            }
            myContext = it
        }
        super.onEnabled(context)
    }

    /**
     * Stop listening for events, and free refs
     */
    override fun onDisabled(context: Context?) {
        val mgr = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        mgr?.unregisterListener(this)
        // stored context no longer valid, free it
        myContext = null
        super.onDisabled(context)
    }

    /**
     * Updates the text displayed by the widget with ID `appWidgetId`.
     */
    private fun updateText(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.app_widget_layout)
        val reading = context.getString(R.string.value_display, displayFormat.format(lastReading))
        views.setTextViewText(R.id.tvWidgetDisplay, reading)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    /**
     * We don't use updatePeriodMillis, so onUpdate() gets called when widget is started but not later
     */
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        errorNull (context, appWidgetManager, appWidgetIds, "onUpdate called with incomplete data!")
        { ctx: Context, mgr: AppWidgetManager, ids: IntArray -> ids.forEach { updateText(ctx, mgr, it) } }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // don't care
    }

    /**
     * Programmatically trigger the onUpdate event for each active widget.
     */
    private fun triggerUpdate(appContext: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(appContext)
        val thisWidget = ComponentName(appContext, LightDisplayAppWidgetProvider::class.java)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        if (appWidgetIds != null && appWidgetIds.isNotEmpty()) {
            /**
             * see https://stackoverflow.com/a/7738687/132510
             */
            val intent = Intent(appContext, LightDisplayAppWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
            // since it seems the onUpdate() is only fired on that:
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
            appContext.sendBroadcast(intent)
        } else {
            Timber.e("triggerUpdate failed to get app widget ID")
        }
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
                errorNull(myContext?.applicationContext,
                    "unable to get app context in onSensorChanged") { triggerUpdate(it) }
            }
        }
    }

    companion object {
        // instances come and go, but we need to keep this. fortunately it's the same for all instances.
        var lastReading: Float = 723.0f
        val displayFormat = DecimalFormat("#,##0")
    }
}
