package com.essential.audio.utils

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build

/**
 * Created by dongc on 8/30/2017.
 */
class MediaController private constructor() {
    val player: MediaPlayer = MediaPlayer()
        get() = field

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            player.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
        } else {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
    }

    companion object {
        val instance: MediaController by lazy { MediaController() }
    }

    fun prepare(url: String) {
        player.setDataSource(url)
        player.prepareAsync()
    }

    fun resume() {
        if (!player.isPlaying) {
            player.start()
        }
    }

    fun pause() {
        if (player.isPlaying)
            player.pause()
    }

    fun stop() {
        if (player.isPlaying) {
            player.stop()
        }
        player.reset()
    }

    fun seekTo(milliSecond: Int) {
        player.seekTo(milliSecond)
    }

    fun isPlaying(): Boolean = player.isPlaying

    fun getDuration(): Int = player.duration

    fun setOnPreparedListener(onPreparedListener: MediaPlayer.OnPreparedListener) {
        player.setOnPreparedListener(onPreparedListener)
    }

    fun removeOnPreparedListener() {
        player.setOnPreparedListener(null)
    }

    fun setOnBufferingUpdateListener(onBufferingUpdateListener: MediaPlayer.OnBufferingUpdateListener) {
        player.setOnBufferingUpdateListener(onBufferingUpdateListener)
    }

    fun removeOnBufferingUpdateListener() {
        player.setOnBufferingUpdateListener(null)
    }

    fun setOnCompleteListener(onCompletionListener: MediaPlayer.OnCompletionListener) {
        player.setOnCompletionListener(onCompletionListener)
    }

    fun removeOnCompleteListener() {
        player.setOnCompletionListener(null)
    }

    fun dispose() {
        if (player != null) {
            if (player.isPlaying)
                player.stop()
            player.release()
        }
    }
}