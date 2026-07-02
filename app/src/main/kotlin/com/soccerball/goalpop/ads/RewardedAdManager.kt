package com.soccerball.goalpop.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.startapp.sdk.adsbase.Ad
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppAd.AdMode
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import com.startapp.sdk.adsbase.adlisteners.VideoListener
import java.lang.ref.WeakReference

class RewardedAdManager private constructor(
    private val application: Application,
) : Application.ActivityLifecycleCallbacks {

    private var activityRef = WeakReference<Activity>(null)
    private var rewardedAd: StartAppAd? = null
    private var isLoaded = false
    private var isLoading = false
    private var pendingReward: (() -> Unit)? = null
    private var pendingFailure: (() -> Unit)? = null

    val isReady: Boolean
        get() = isLoaded && rewardedAd?.isReady == true

    fun attach() {
        application.registerActivityLifecycleCallbacks(this)
        StartAppAd.disableAutoInterstitial()
        StartAppAd.disableSplash()
        preload()
    }

    fun preload() {
        if (isLoading || isLoaded) return
        val context = activityRef.get() ?: application
        isLoading = true
        val ad = StartAppAd(context)
        rewardedAd = ad
        ad.loadAd(AdMode.REWARDED_VIDEO, object : AdEventListener {
            override fun onReceiveAd(received: Ad) {
                isLoaded = true
                isLoading = false
            }

            override fun onFailedToReceiveAd(received: Ad?) {
                rewardedAd = null
                isLoaded = false
                isLoading = false
            }
        })
    }

    fun showRewarded(
        onReward: () -> Unit,
        onFailed: () -> Unit = {},
    ) {
        val ad = rewardedAd
        if (activityRef.get() == null || ad == null || !ad.isReady) {
            onFailed()
            preload()
            return
        }

        pendingReward = onReward
        pendingFailure = onFailed

        ad.setVideoListener(object : VideoListener {
            override fun onVideoCompleted() {
                pendingReward?.invoke()
                clearPending()
                resetAndPreload()
            }
        })

        val shown = ad.showAd()
        if (!shown) {
            pendingFailure?.invoke()
            clearPending()
            resetAndPreload()
        } else {
            isLoaded = false
        }
    }

    private fun resetAndPreload() {
        rewardedAd = null
        isLoaded = false
        preload()
    }

    private fun clearPending() {
        pendingReward = null
        pendingFailure = null
    }

    override fun onActivityResumed(activity: Activity) {
        activityRef = WeakReference(activity)
        if (!isLoaded && !isLoading) {
            preload()
        }
    }

    override fun onActivityPaused(activity: Activity) {
        if (activityRef.get() === activity) {
            activityRef = WeakReference(null)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
    override fun onActivityStarted(activity: Activity) = Unit
    override fun onActivityStopped(activity: Activity) = Unit
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
    override fun onActivityDestroyed(activity: Activity) = Unit

    companion object {
        @Volatile
        private var instance: RewardedAdManager? = null

        fun getInstance(application: Application): RewardedAdManager {
            return instance ?: synchronized(this) {
                instance ?: RewardedAdManager(application).also { instance = it }
            }
        }
    }
}
