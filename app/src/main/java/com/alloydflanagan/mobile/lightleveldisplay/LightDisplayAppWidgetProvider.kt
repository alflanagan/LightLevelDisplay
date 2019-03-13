package com.alloydflanagan.mobile.lightleveldisplay

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.RemoteViews
import timber.log.Timber
import java.text.DecimalFormat
import kotlin.math.abs

class LightDisplayAppWidgetProvider: AppWidgetProvider(), SensorEventListener {
    private var sensorManager: SensorManager? = null

    private var lightSensor: Sensor? = null

    private var lastReading: Float = 0.0f

    private var displayFormat = DecimalFormat("#,##0.00")

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        sensorManager = context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (lightSensor != null) sensorManager?.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        sensorManager?.unregisterListener(this)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if (context != null && appWidgetManager != null && appWidgetIds != null) {
            Timber.d("updating widget")
            val views = RemoteViews(context.packageName, R.layout.app_widget_layout)
            views.setTextViewText(R.id.tvWidgetDisplay, context.getString(R.string.value_display, displayFormat.format(lastReading)))
            for (appWidgetId in appWidgetIds) {
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // don't care
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && abs(event.values[0] - lastReading) > 0.01f) lastReading = event.values[0]
    }
}