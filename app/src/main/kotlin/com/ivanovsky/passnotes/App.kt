package com.ivanovsky.passnotes

import androidx.multidex.MultiDexApplication
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.ivanovsky.passnotes.domain.LoggerInteractor
import com.ivanovsky.passnotes.injection.KoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(BuildConfig.IS_CRASHLYTICS_ENABLED)

        val loggerInteractor = LoggerInteractor(this)
            .apply {
                initialize()
            }

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(KoinModule.buildModule(loggerInteractor))
        }
    }
}