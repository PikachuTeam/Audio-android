package com.essential.audio.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.essential.audio.utils.Constants
import com.essential.audio.utils.MediaController
import com.essential.audio.utils.NotificationHelper
import com.essential.audio.utils.OnMediaStateListener

/**
 * Created by dongc on 9/2/2017.
 */
class MediaService : Service() {
    private val TAG = MediaService::class.java.simpleName

    private lateinit var mNotificationHelper: NotificationHelper
    private lateinit var mMediaController: MediaController

    private var mPaused: Boolean = false

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
            when (intent.action) {
                Constants.Action.MEDIA_START -> {
                    mPaused = false
                    mMediaController.start()
                    sendBroadcast(Intent(Constants.Action.MEDIA_PREPARING).apply {
                        putExtra(Constants.Extra.AUDIO_NAME, mMediaController.getCurrentAudio().name)
                    })
                    mNotificationHelper.createNotification(mMediaController.getCurrentAudio(), false, true, true)
                }
                Constants.Action.MEDIA_SEEK_TO -> {
                    mMediaController.seekTo(intent.getIntExtra(Constants.Extra.PROGRESS, 0))
                }
                else -> executeWork(intent)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        unregisterReceiver(mMediaControlReceiver)
        mMediaController.removeOnPreparedListener()
        mNotificationHelper.deleteNotification()
        super.onDestroy()
    }

    private fun executeWork(intent: Intent?) {
        var isPlaying = true
        var onGoing = true
        intent?.run {
            when (intent.action) {
                Constants.Action.MEDIA_PLAY -> {
                    mMediaController.play()
                    mPaused = false
                }
                Constants.Action.MEDIA_PREVIOUS -> {
                    mMediaController.previous()
                    mPaused = false
                }
                Constants.Action.MEDIA_NEXT -> {
                    mMediaController.next()
                    mPaused = false
                }
                Constants.Action.MEDIA_PAUSE -> {
                    mMediaController.pause()
                    isPlaying = false
                    onGoing = false
                    mPaused = true
                }
            }
        }
        mNotificationHelper.createNotification(mMediaController.getCurrentAudio(), false, isPlaying, onGoing)
    }

    private fun setEventListeners() {
        mMediaController.setOnPreparedListener(MediaPlayer.OnPreparedListener {
            if (!mPaused) {
                mMediaController.seekTo(0)
                mMediaController.play()
                sendBroadcast(Intent(Constants.Action.MEDIA_PREPARED).apply {
                    putExtra(Constants.Extra.DURATION, mMediaController.player.duration)
                })
            }
        })

        mMediaController.setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener { _, i ->
            sendBroadcast(Intent(Constants.Action.MEDIA_BUFFERING).apply { putExtra(Constants.Extra.PROGRESS, i) })
        })

        mMediaController.setOnMediaPlayerStateListener(object : OnMediaStateListener {
            override fun onFinishPlaying() {
                mNotificationHelper.createNotification(mMediaController.getCurrentAudio(), false, false, false)
            }

            override fun onAudioCompleted() {
                sendBroadcast(Intent(Constants.Action.MEDIA_AUDIO_COMPLETED))
            }
        })
    }
}