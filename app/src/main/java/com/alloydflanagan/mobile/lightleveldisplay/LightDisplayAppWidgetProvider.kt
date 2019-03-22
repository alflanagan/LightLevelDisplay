package com.alloydflanagan.mobile.lightleveldisplay

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import timber.log.Timber
import java.text.DecimalFormat

class LightDisplayAppWidgetProvider: LoggedAppWidgetProvider() {

    private var lightReading = 0.0f

    private var serviceStarted = false

    // seems like onReceive, onUpdate, and onDeleted are called in tests
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            Timber.d("received Intent: $intent")
            lightReading = intent.getFloatExtra(LightSensorService.READING_KEY, 0.0f)
            if (context != null) triggerUpdate(context)
        }
        if (context != null && !serviceStarted) {
            val startIntent = Intent(context, LightSensorService::class.java)
            context.startService(startIntent)
            serviceStarted = true
        }
        super.onReceive(context, intent)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        if (serviceStarted) {
            val intent = Intent(context, LightSensorService::class.java)
            context?.stopService(intent)
            serviceStarted = false
        }
        super.onDeleted(context, appWidgetIds)
    }

    // but perhaps we'd better override any method that seems applicable??
    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

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

    /**
     * Updates the text displayed by the widget with ID `appWidgetId`.
     */
    private fun updateText(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, lastReading: Float) {
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
        { ctx: Context, mgr: AppWidgetManager, ids: IntArray -> ids.forEach { updateText(ctx, mgr, it, 0.0f) } }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
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

    companion object {
        val displayFormat = DecimalFormat("#,##0")
    }
}
