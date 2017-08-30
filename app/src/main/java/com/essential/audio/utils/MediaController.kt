package com.essential.audio.utils

import android.media.MediaPlayer

/**
 * Created by dongc on 8/30/2017.
 */
class MediaController private constructor() {
    private lateinit var mPlayer: MediaPlayer

    private object Holder {
        val INSTANCE = MediaController()
    }

    companion object {
        val instance: MediaController by lazy { Holder.INSTANCE }
    }

    fun load(url: String) {

    }

    fun play() {

    }

    fun pause() {

    }

    fun stop() {

    }
}