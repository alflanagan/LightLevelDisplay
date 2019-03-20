package com.alloydflanagan.mobile.lightleveldisplay

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import timber.log.Timber

/**
 * A simple subclass to just log each callback. I find manipulating the widget and watching the log more helpful
 * than Android documentation.
 */
open class LoggedAppWidgetProvider: AppWidgetProvider() {

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        Timber.d("onUpdate()")
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.d("onReceive()")
        super.onReceive(context, intent)
    }

    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
        Timber.d("onRestore()")
        super.onRestored(context, oldWidgetIds, newWidgetIds)
    }

    override fun onEnabled(context: Context?) {
        Timber.d("onEnabled()")
        super.onEnabled(context)
    }

    override fun onDisabled(context: Context?) {
        Timber.d("onDisabled()")
        super.onDisabled(context)
    }

    override fun peekService(myContext: Context?, service: Intent?): IBinder {
        Timber.d("peekService()")
        return super.peekService(myContext, service)
    }

    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
        Timber.d("onDeleted()")
        super.onDeleted(context, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        Timber.d("onAppWidgetOptionsChanged()")
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }
}
