package com.essential.audio.utils

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import com.essential.audio.data.model.Audio

/**
 * Created by dongc on 8/30/2017.
 */
class MediaController {
    // Properties
    val player: MediaPlayer = MediaPlayer()
        get() = field

    var currentPosition = -1
        get() = field
        set(value) {
            field = value
        }

    var category = -1
        get() = field
        set(value) {
            field = value
        }

    private var mOnMediaStateListener: OnMediaStateListener? = null

    var audios: MutableList<Audio> = ArrayList()
        get() = field
        set(value) {
            field = value
        }

    // Constructor
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            player.setAudioAttributes(AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build())
        } else {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }

        player.setOnCompletionListener {
            currentPosition++
            if (currentPosition >= audios.size) {
                currentPosition = audios.size - 1
                pause()
                mOnMediaStateListener?.onFinishPlaying()
            } else {
                mOnMediaStateListener?.onAudioCompleted()
                prepare(audios[currentPosition])
            }
        }

        player.reset()
    }

    // Functions
    fun setOnMediaPlayerStateListener(onMediaStateListener: OnMediaStateListener?) {
        mOnMediaStateListener = onMediaStateListener
    }

    fun start() {
        prepare(audios[currentPosition])
    }

    fun play() {
        player.start()
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

    fun next() {
        currentPosition++
        if (currentPosition >= audios.size) {
            currentPosition = audios.size - 1
            seekTo(0)
            if (!player.isPlaying)
                player.start()
            return
        }
        stop()
        prepare(audios[currentPosition])
    }

    fun previous() {
        currentPosition--
        if (currentPosition < 0) {
            currentPosition = 0
            seekTo(0)
            if (!player.isPlaying)
                player.start()
            return
        }
        stop()
        prepare(audios[currentPosition])
    }

    fun isPlaying(): Boolean = player.isPlaying

    fun seekTo(milliSecond: Int) {
        player.seekTo(milliSecond)
    }

    fun setOnPreparedListener(onPreparedListener: MediaPlayer.OnPreparedListener) {
        player.setOnPreparedListener(onPreparedListener)
    }

    fun removeOnPreparedListener() {
        player.setOnPreparedListener(null)
    }

    fun getCurrentAudio(): Audio = audios[currentPosition]

    fun setOnBufferingUpdateListener(onBufferingUpdateListener: MediaPlayer.OnBufferingUpdateListener) {
        player.setOnBufferingUpdateListener(onBufferingUpdateListener)
    }

    fun removeOnBufferingUpdateListener() {
        player.setOnBufferingUpdateListener(null)
    }

    fun dispose() {
        if (player.isPlaying)
            player.stop()
        player.release()
    }

    private fun prepare(audio: Audio) {
        player.setDataSource(audio.url)
        player.prepareAsync()
    }
}