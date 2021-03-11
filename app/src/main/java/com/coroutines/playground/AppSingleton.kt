package com.coroutines.playground

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AppSingleton : Application() {

    override fun onCreate() {
        super.onCreate()

        // Timber
        Timber.plant(Timber.DebugTree())
    }
}