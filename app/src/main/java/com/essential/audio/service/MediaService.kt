package com.essential.audio.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.essential.audio.data.model.Audio
import com.essential.audio.utils.Constants
import com.essential.audio.utils.JsonHelper
import com.essential.audio.utils.NotificationHelper

/**
 * Created by dongc on 9/2/2017.
 */
class MediaService : Service() {
    private val TAG = MediaService::class.java.simpleName

    private lateinit var mNotificationHelper: NotificationHelper

    private val mMediaControlReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                when (intent.action) {
                    Constants.Action.PLAY -> Log.e(TAG, "Play")
                    Constants.Action.PAUSE -> Log.e(TAG, "Pause")
                    Constants.Action.PREVIOUS -> Log.e(TAG, "Previous")
                    Constants.Action.NEXT -> Log.e(TAG, "Next")
                    else -> Log.e(TAG, "Default")
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(mMediaControlReceiver, IntentFilter().apply {
            addAction(Constants.Action.PLAY)
            addAction(Constants.Action.PAUSE)
            addAction(Constants.Action.NEXT)
            addAction(Constants.Action.PREVIOUS)
        })

        mNotificationHelper = NotificationHelper(applicationContext)
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.run {
            when (intent.action) {
                Constants.Action.PLAY -> {
                    val audioJson = intent.getStringExtra(Constants.Extra.CHOSEN_AUDIO)
                    mNotificationHelper.createNotification(JsonHelper.instance.fromJson(audioJson, Audio::class.java), false)
                }
                else -> Log.e(TAG, "Aloha")
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        unregisterReceiver(mMediaControlReceiver)
        super.onDestroy()
    }
}