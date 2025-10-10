package com.animeybe.spacelaunchcompanion

import android.app.Application
import com.animeybe.spacelaunchcompanion.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SpacelaunchCompanionApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SpacelaunchCompanionApplication)
            modules(appModule)
        }
    }
}