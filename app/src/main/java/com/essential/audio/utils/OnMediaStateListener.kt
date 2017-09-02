package com.essential.audio.utils

/**
 * Created by dongc on 9/2/2017.
 */
interface OnMediaStateListener {
    fun onStartLoading(name: String)

    fun onCompletePlaying()
}