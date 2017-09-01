package com.essential.audio.utils

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build

/**
 * Created by dongc on 8/30/2017.
 */
class MediaController private constructor() : MediaPlayer.OnPreparedListener {

    private val mPlayer: MediaPlayer = MediaPlayer()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPlayer.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
        } else {
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
        mPlayer.setOnPreparedListener(this)
    }

    private object Holder {
        val INSTANCE = MediaController()
    }

    companion object {
        val instance: MediaController by lazy { Holder.INSTANCE }
    }

    fun play(url: String) {
        mPlayer.setDataSource(url)
        mPlayer.prepareAsync()
    }

    fun pause() {
        if (mPlayer.isPlaying)
            mPlayer.pause()
    }

    fun stop() {
        if (mPlayer.isPlaying) {
            mPlayer.stop()
        }
    }

    fun seekTo(milliSecond: Int) {
        mPlayer.seekTo(milliSecond)
    }

    fun isPlaying(): Boolean = mPlayer.isPlaying

    fun getDuration(): Int = mPlayer.duration

    override fun onPrepared(p0: MediaPlayer?) {
        p0?.start()
    }
}