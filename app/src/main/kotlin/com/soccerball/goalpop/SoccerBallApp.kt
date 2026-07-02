package com.soccerball.goalpop

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.soccerball.goalpop.ads.RewardedAdManager
import com.soccerball.goalpop.analytics.Analytics
import com.soccerball.goalpop.data.GamePreferences
import com.startapp.sdk.adsbase.StartAppSDK
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

class SoccerBallApp : Application() {
    lateinit var preferences: GamePreferences
        private set

    override fun onCreate() {
        super.onCreate()
        preferences = GamePreferences(this)

        val metricaConfig = AppMetricaConfig
            .newConfigBuilder(APPMETRICA_API_KEY)
            .build()
        AppMetrica.activate(this, metricaConfig)
        AppMetrica.enableActivityAutoTracking(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) {
                    Analytics.reportAppClose()
                }
            },
        )

        StartAppSDK.initParams(applicationContext, STARTAPP_APP_ID)
            .setReturnAdsEnabled(false)
            .init()

        RewardedAdManager.getInstance(this).attach()

        if (BuildConfig.DEBUG) {
            StartAppSDK.setTestAdsEnabled(true)
        }
    }

    companion object {
        private const val APPMETRICA_API_KEY = "a1ff7413-42d7-4417-ae78-9598f0a68a88"
        private const val STARTAPP_APP_ID = "206994821"
    }
}
