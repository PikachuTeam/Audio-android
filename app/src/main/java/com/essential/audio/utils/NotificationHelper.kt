package com.essential.audio.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import com.essential.audio.R
import com.essential.audio.data.model.Audio

/**
 * Created by dongc on 9/2/2017.
 */
class NotificationHelper(private var context: Context) {
    private val CHANNEL_ID = "1"
    private val NOTIFICATION_ID = 2

    private val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val mPreviousIntent = Intent(Constants.Action.PREVIOUS)
    private val mPlayIntent = Intent(Constants.Action.PLAY)
    private val mPauseIntent = Intent(Constants.Action.PAUSE)
    private val mNextIntent = Intent(Constants.Action.NEXT)

    private val mPreviousPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 1, mPreviousIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    private val mPlayPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 1, mPlayIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    private val mPausePendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 1, mPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    private val mNextPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 1, mNextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

    fun createNotification(audio: Audio, cancelable: Boolean) {
        mNotificationManager.notify(NOTIFICATION_ID, NotificationCompat.Builder(context, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(cancelable)
                .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
                .addAction(R.drawable.ic_previous_audio, "Previous", mPreviousPendingIntent)
                .addAction(R.drawable.ic_pause, "Pause", mPausePendingIntent)
                .addAction(R.drawable.ic_next_audio, "Next", mNextPendingIntent)
                .setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(1))
                .setContentTitle(audio.name)
                .build())
    }
}