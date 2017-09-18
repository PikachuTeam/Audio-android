package com.essential.audio.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.essential.audio.data.model.Audio
import com.essential.audio.utils.*
import com.google.gson.reflect.TypeToken

/**
 * Created by dongc on 9/2/2017.
 */
class MediaService : Service() {
  private val TAG = MediaService::class.java.simpleName
  private val UPDATE_PROGRESS_INTERVAL = 1000L

  private lateinit var mNotificationHelper: NotificationHelper
  private lateinit var mMediaController: MediaController

  private var mPaused: Boolean = false
  private var mIsUpdatingProgress = false

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

    setEventListeners()
  }

  override fun onBind(p0: Intent?): IBinder {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    intent?.run {
      when (action) {
        Constants.Action.MEDIA_START -> {
          val chosenAudio = getIntExtra(Constants.Extra.CHOSEN_AUDIO, 0)
          mPaused = false
          if (!mMediaController.isPlaying(chosenAudio)) {
            mMediaController.audios = JsonHelper.instance.fromJson(
                    getStringExtra(Constants.Extra.AUDIOS),
                    genericType<MutableList<Audio>>())
            mMediaController.currentPosition = chosenAudio

            mMediaController.start()

            LocalBroadcastManager.getInstance(this@MediaService).sendBroadcast(Intent(Constants.Action.MEDIA_PREPARING).apply {
              putExtra(Constants.Extra.AUDIO_NAME, mMediaController.getCurrentAudio().name)
            })

            mNotificationHelper.createNotification(mMediaController.getCurrentAudio(), false, true, true)
          } else {
            mMediaController.play()
          }
        }
        Constants.Action.MEDIA_SEEK_TO -> {
          mMediaController.seekTo(getIntExtra(Constants.Extra.PROGRESS, 0))
        }
        Constants.Action.MEDIA_GET_CURRENT_STATE -> {
          LocalBroadcastManager.getInstance(this@MediaService)
                  .sendBroadcast(Intent(Constants.Action.MEDIA_GET_CURRENT_STATE).apply {
                    putExtra(Constants.Extra.IS_PLAYING, mMediaController.isPlaying())
                    putExtra(
                            Constants.Extra.AUDIO_NAME,
                            mMediaController.getCurrentAudio().name
                    )
                    putExtra(
                            Constants.Extra.PROGRESS,
                            mMediaController.player.currentPosition
                    )
                    putExtra(
                            Constants.Extra.DURATION,
                            mMediaController.player.duration
                    )
                    putExtra(
                            Constants.Extra.IS_PREPARING,
                            mMediaController.isPreparing
                    )
                  })
        }
        Constants.Action.MEDIA_UPDATE_LIST -> {
          mMediaController.audios = JsonHelper.instance.fromJson(
                  getStringExtra(Constants.Extra.AUDIOS),
                  genericType<MutableList<Audio>>())
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
    var isPlaying = true
    var onGoing = true
    intent?.run {
      when (intent.action) {
        Constants.Action.MEDIA_PLAY -> {
          LocalBroadcastManager.getInstance(this@MediaService)
                  .sendBroadcast(Intent(Constants.Action.MEDIA_PLAY))

          mTimeHandler?.post(mUpdateTimeTask)
          mMediaController.play()
          mPaused = false
        }
        Constants.Action.MEDIA_PREVIOUS -> {
          if (mPaused) {
            mTimeHandler?.post(mUpdateTimeTask)
          }
          mMediaController.previous()
          LocalBroadcastManager.getInstance(this@MediaService)
                  .sendBroadcast(Intent(Constants.Action.MEDIA_PREVIOUS).apply {
                    putExtra(
                            Constants.Extra.CURRENT_AUDIO,
                            JsonHelper.instance.toJson(mMediaController.getCurrentAudio())
                    )
                  })
          mPaused = false
        }
        Constants.Action.MEDIA_NEXT -> {
          if (mPaused) {
            mTimeHandler?.post(mUpdateTimeTask)
          }
          mMediaController.next()
          LocalBroadcastManager.getInstance(this@MediaService)
                  .sendBroadcast(Intent(Constants.Action.MEDIA_NEXT).apply {
                    putExtra(
                            Constants.Extra.CURRENT_AUDIO,
                            JsonHelper.instance.toJson(mMediaController.getCurrentAudio())
                    )
                  })
          mPaused = false
        }
        Constants.Action.MEDIA_PAUSE -> {
          LocalBroadcastManager.getInstance(this@MediaService)
                  .sendBroadcast(Intent(Constants.Action.MEDIA_PAUSE))

          mTimeHandler?.removeCallbacks(mUpdateTimeTask)

          mMediaController.pause()
          isPlaying = false
          onGoing = false
          mPaused = true
        }
      }
    }
    mNotificationHelper.createNotification(
            mMediaController.getCurrentAudio(),
            false,
            isPlaying,
            onGoing)
  }

  private fun setEventListeners() {
    mMediaController.setOnPreparedListener(MediaPlayer.OnPreparedListener {
      mMediaController.isPreparing = false
      if (!mPaused) {
        mMediaController.seekTo(0)
        mMediaController.play()
        mTimeHandler?.post(mUpdateTimeTask)
        LocalBroadcastManager.getInstance(this@MediaService)
                .sendBroadcast(Intent(Constants.Action.MEDIA_PREPARED).apply {
                  putExtra(Constants.Extra.DURATION, mMediaController.player.duration)
                })
      }
    })

    mMediaController.setOnBufferingUpdateListener(
            MediaPlayer.OnBufferingUpdateListener { _, i ->
              LocalBroadcastManager.getInstance(this@MediaService)
                      .sendBroadcast(Intent(Constants.Action.MEDIA_BUFFERING).apply {
                        putExtra(Constants.Extra.PROGRESS, i)
                      })
            })

    mMediaController.setOnMediaPlayerStateListener(object : OnMediaStateListener {
      override fun onFinishPlaying() {
        mNotificationHelper.createNotification(
                mMediaController.getCurrentAudio(),
                false,
                false,
                false
        )
        mTimeHandler?.removeCallbacks(mUpdateTimeTask)
        LocalBroadcastManager.getInstance(this@MediaService)
                .sendBroadcast(Intent(Constants.Action.MEDIA_FINISH_PLAYING))
      }

      override fun onAudioCompleted() {
        LocalBroadcastManager.getInstance(this@MediaService)
                .sendBroadcast(Intent(Constants.Action.MEDIA_AUDIO_COMPLETED))
      }
    })
  }

  private inline fun <reified T> genericType() = object : TypeToken<T>() {}.type
}