package com.essentd.TDAudio.utils

import TDAudio.BuildConfig
import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.startapp.android.publish.adsCommon.Ad
import com.startapp.android.publish.adsCommon.StartAppAd
import com.startapp.android.publish.adsCommon.StartAppSDK
import com.startapp.android.publish.adsCommon.VideoListener
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener

/**
 * Created by dong on 24/09/2017.
 */
class AdsController constructor(context: Context) {
  private val GOOGLE_VIDEO_AD_TEST_ID = "ca-app-pub-3940256099942544/5224354917"

  private val mGoogleVideoAd: RewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context)
  private val mStartAppAd: StartAppAd = StartAppAd(context)

  private var mIsGoogleAdTurn = true

  companion object {
    val ALL = 1
    val GOOGLE = 2
    val START_APP = 3

    private val START_APP_AD_ID = "208237699"

    var adMode = ALL

    fun init(activity: Activity) {
      StartAppSDK.init(activity, START_APP_AD_ID, false)
      StartAppAd.disableSplash()
      StartAppAd.disableAutoInterstitial()
    }
  }

  fun loadAd() {
    when (adMode) {
      GOOGLE -> {
        Log.e("Loadng", "Google")
        loadGoogleVideo()
      }
      START_APP -> {
        Log.e("Loadng", "StartApp")
        loadStartAppVideo()
      }
      else -> {
        if (mIsGoogleAdTurn) {
          Log.e("AdController", "google")
          loadGoogleVideo()
        } else {
          Log.e("AdController", "admob")
          loadStartAppVideo()
        }
      }
    }
  }

  fun showAd() {
    if (mIsGoogleAdTurn) {
      mIsGoogleAdTurn = false
      showGoogleVideo()
    } else {
      mIsGoogleAdTurn = true
      showStartAppVideo()
    }
  }

  fun setGoogleVideoAdListener(rewardedVideoAdListener: RewardedVideoAdListener) {
    mGoogleVideoAd.rewardedVideoAdListener = rewardedVideoAdListener
  }

  private fun loadGoogleVideo() {
    if (!mGoogleVideoAd.isLoaded) {
      mIsGoogleAdTurn=true
      if (BuildConfig.DEBUG)
        mGoogleVideoAd.loadAd(
                GOOGLE_VIDEO_AD_TEST_ID,
                AdRequest.Builder().build()
        )
      else
        mGoogleVideoAd.loadAd(
                BuildConfig.GG_AD_REAL_ID,
                AdRequest.Builder().build()
        )
    }
  }

  private fun showGoogleVideo(): Boolean {
    if (mGoogleVideoAd.isLoaded) {
      mGoogleVideoAd.show()
      return true
    }
    return false
  }

  fun setStartAppVideoAdListener(startAppAdVideoListener: VideoListener) {
    mStartAppAd.setVideoListener(startAppAdVideoListener)

  }

  private fun loadStartAppVideo() {
    mIsGoogleAdTurn=false
    mStartAppAd.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, object : AdEventListener {
      override fun onReceiveAd(ad: Ad?) {
      }

      override fun onFailedToReceiveAd(ad: Ad?) {
      }
    })
  }

  private fun showStartAppVideo() {
    mStartAppAd.showAd()
  }
}