package com.essentd.TDAudio.service

import TDAudio.R
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.view.WindowManager
import android.widget.Toast
import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.data.model.AudioState
import com.essentd.TDAudio.utils.*
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.gson.reflect.TypeToken
import com.startapp.android.publish.adsCommon.VideoListener

/**
 * Created by dongc on 9/2/2017.
 */
class MediaService : Service() {
  private val TAG = MediaService::class.java.simpleName
  private val UPDATE_PROGRESS_INTERVAL = 1000L

  private lateinit var mNotificationHelper: NotificationHelper
  private lateinit var mMediaController: MediaController
  private lateinit var mAdController: AdsController

  private var mPaused: Boolean = false
  private var mUnlocked: Boolean = false

  private var mTimeHandler: Handler? = Handler()
  private val mUpdateTimeTask: Runnable = object : Runnable {
    override fun run() {
      if (mMediaController.isPlaying()) {
        LocalBroadcastManager.getInstance(this@MediaService).sendBroadcast(Intent(Constants.Action.MEDIA_UPDATE_PROGRESS).apply {
          putExtra(Constants.Extra.PROGRESS, mMediaController.player.currentPosition)
          putExtra(Constants.Extra.DURATION, mMediaController.player.duration)
        })
        mTimeHandler?.postDelayed(this, UPDATE_PROGRESS_INTERVAL)
      }
    }
  }

  private val mMediaControlReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      executeWork(intent)
    }
  }

  override fun onCreate() {
    super.onCreate()
    registerReceiver(mMediaControlReceiver, IntentFilter().apply {
      addAction(Constants.Action.MEDIA_PLAY)
      addAction(Constants.Action.MEDIA_PAUSE)
      addAction(Constants.Action.MEDIA_NEXT)
      addAction(Constants.Action.MEDIA_PREVIOUS)
    })

    mNotificationHelper = NotificationHelper(applicationContext)
    mMediaController = MediaController()
    mAdController = AdsController(this)

    setEventListeners()
  }

  override fun onBind(p0: Intent?): IBinder {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    intent?.run {
      when (action) {
        Constants.Action.INIT -> {
//          mAdController.loadGoogleVideo()
          mAdController.loadAd()
        }
        Constants.Action.MEDIA_START -> {
          val chosenAudio = getIntExtra(Constants.Extra.CHOSEN_AUDIO, 0)
          if (!mMediaController.isCurrentAudio(chosenAudio)) {
            mPaused = false
            mMediaController.audios = JsonHelper.instance.fromJson(
                    getStringExtra(Constants.Extra.AUDIOS),
                    genericType<MutableList<Audio>>())
            mMediaController.currentPosition = chosenAudio

            mMediaController.start()
          } else {
            if (mPaused) {
              mPaused = false
              mTimeHandler?.post(mUpdateTimeTask)
            }
            mMediaController.play()
          }
        }
        Constants.Action.MEDIA_SEEK_TO -> {
          mMediaController.seekTo(getIntExtra(Constants.Extra.PROGRESS, 0))
        }
        Constants.Action.MEDIA_GET_CURRENT_STATE -> {
          LocalBroadcastManager.getInstance(this@MediaService)
                  .sendBroadcast(Intent(Constants.Action.MEDIA_GET_CURRENT_STATE).apply {
                    putExtra(
                            Constants.Extra.PROGRESS,
                            if (mMediaController.isPlaying()) 0 else mMediaController.player.currentPosition
                    )
                    putExtra(
                            Constants.Extra.DURATION,
                            if (mMediaController.isPreparing) 0 else mMediaController.player.duration
                    )
                    putExtra(
                            Constants.Extra.CURRENT_AUDIO,
                            JsonHelper.instance.toJson(mMediaController.getCurrentAudio())
                    )
                  })
        }
        Constants.Action.MEDIA_UPDATE_LIST -> {
          if (getBooleanExtra(Constants.Extra.UPDATE_CONTROLLER, false)) {
            mMediaController.audios = JsonHelper.instance.fromJson(
                    getStringExtra(Constants.Extra.AUDIOS),
                    genericType<MutableList<Audio>>())
          } else {
            LocalBroadcastManager.getInstance(this@MediaService)
                    .sendBroadcast(Intent(Constants.Action.MEDIA_UPDATE_LIST).apply {
                      putExtra(Constants.Extra.AUDIOS, JsonHelper.instance.toJson(mMediaController.audios))
                    })
          }
        }
        else -> executeWork(intent)
      }
    }
    return START_STICKY
  }

  override fun onDestroy() {
    unregisterReceiver(mMediaControlReceiver)
    mMediaController.dispose()
    mMediaController.removeOnPreparedListener()
    mNotificationHelper.deleteNotification()
    mTimeHandler?.removeCallbacks(mUpdateTimeTask)
    super.onDestroy()
  }

  private fun executeWork(intent: Intent?) {
    intent?.run {
      when (action) {
        Constants.Action.MEDIA_PLAY -> {
          mTimeHandler?.post(mUpdateTimeTask)
          mMediaController.play()
          mPaused = false
        }
        Constants.Action.MEDIA_PREVIOUS -> {
          if (mPaused) {
            mTimeHandler?.post(mUpdateTimeTask)
          }
          mMediaController.previous()
          mPaused = false
        }
        Constants.Action.MEDIA_NEXT -> {
          if (mPaused) {
            mTimeHandler?.post(mUpdateTimeTask)
          }
          mMediaController.next()
          mPaused = false
        }
        Constants.Action.MEDIA_PAUSE -> {
          mTimeHandler?.removeCallbacks(mUpdateTimeTask)

          mMediaController.pause()
          mPaused = true
        }
      }
    }
  }

  private fun setEventListeners() {
    mMediaController.setOnPreparedListener(MediaPlayer.OnPreparedListener {
      mMediaController.isPreparing = false
      if (!mPaused)
        mMediaController.prepared()
    })

    mMediaController.setOnBufferingUpdateListener(
            MediaPlayer.OnBufferingUpdateListener { _, i ->
              LocalBroadcastManager.getInstance(this@MediaService)
                      .sendBroadcast(Intent(Constants.Action.MEDIA_BUFFERING).apply {
                        putExtra(Constants.Extra.PROGRESS, i)
                      })
            })

    mMediaController.onLockedAudioChoose = { audio ->
      mUnlocked = false
      val dialog = AlertDialog.Builder(this, R.style.AppTheme_MaterialDialog)
              .setMessage(getString(R.string.locked_item))
              .setPositiveButton(getString(R.string.watch_to_unlock)) { d, _ ->
                d.dismiss()
                mAdController.showAd()
              }
              .setNegativeButton(getString(R.string.cancel)) { d, _ ->
                d.dismiss()
                Toast.makeText(this, "Still lock", Toast.LENGTH_SHORT).show()
              }
              .create()

      dialog.window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
      dialog.window.attributes.windowAnimations = R.style.DialogAnimation
      dialog.show()
    }

    mMediaController.onMediaStateChanged = { audio ->
      val audioJson = JsonHelper.instance.toJson(audio)
      when (audio.state) {
        AudioState.PREPARING -> {
          LocalBroadcastManager.getInstance(this)
                  .sendBroadcast(Intent(Constants.Action.MEDIA_AUDIO_STATE_CHANGED).apply {
                    putExtra(Constants.Extra.CURRENT_AUDIO, audioJson)
                  })

          mNotificationHelper.createNotification(audio, false, true, true)
        }
        AudioState.PREPARED -> {
          LocalBroadcastManager.getInstance(this)
                  .sendBroadcast(Intent(Constants.Action.MEDIA_AUDIO_STATE_CHANGED).apply {
                    putExtra(Constants.Extra.CURRENT_AUDIO, audioJson)
                  })
          if (!mPaused) {
            mMediaController.play()
          }
        }
        AudioState.PLAYING -> {
          mTimeHandler?.post(mUpdateTimeTask)

          mNotificationHelper.createNotification(audio, false, true, true)

          LocalBroadcastManager.getInstance(this)
                  .sendBroadcast(Intent(Constants.Action.MEDIA_AUDIO_STATE_CHANGED).apply {
                    putExtra(Constants.Extra.CURRENT_AUDIO, audioJson)
                  })
        }
        AudioState.PAUSE -> {
          mTimeHandler?.removeCallbacks(mUpdateTimeTask)

          mNotificationHelper.createNotification(audio, false, false, false)

          LocalBroadcastManager.getInstance(this)
                  .sendBroadcast(Intent(Constants.Action.MEDIA_AUDIO_STATE_CHANGED).apply {
                    putExtra(Constants.Extra.CURRENT_AUDIO, audioJson)
                  })
        }
        AudioState.STOP -> {
          mTimeHandler?.removeCallbacks(mUpdateTimeTask)

          mNotificationHelper.createNotification(audio, false, false, false)

          LocalBroadcastManager.getInstance(this)
                  .sendBroadcast(Intent(Constants.Action.MEDIA_AUDIO_STATE_CHANGED).apply {
                    putExtra(Constants.Extra.CURRENT_AUDIO, audioJson)
                  })
        }
      }
    }

    mAdController.setGoogleVideoAdListener(object : RewardedVideoAdListener {
      override fun onRewardedVideoAdClosed() {
        mAdController.loadAd()
        if (mUnlocked)
          mMediaController.unlockAudio()
      }

      override fun onRewardedVideoAdLeftApplication() {
      }

      override fun onRewardedVideoAdLoaded() {
      }

      override fun onRewardedVideoAdOpened() {
      }

      override fun onRewardedVideoStarted() {
      }

      override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
      }

      override fun onRewarded(rewardItem: RewardItem?) {
        mUnlocked = true
      }
    })

    mAdController.setStartAppVideoAdListener(VideoListener {
      mAdController.showAd()
      mUnlocked = true
      mMediaController.unlockAudio()
    })
  }

  private inline fun <reified T> genericType() = object : TypeToken<T>() {}.type
}