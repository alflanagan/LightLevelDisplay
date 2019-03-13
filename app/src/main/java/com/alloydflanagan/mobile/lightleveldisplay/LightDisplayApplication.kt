package com.alloydflanagan.mobile.lightleveldisplay

import android.app.Application
import timber.log.Timber

class LightDisplayApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}