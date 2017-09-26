package com.essentd.TDAudio.utils

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import android.os.Build
import com.essentd.TDAudio.data.local.CacheHelper
import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.data.model.AudioState

/**
 * Created by dongc on 8/30/2017.
 */
class MediaController {
  // Properties
  val player: MediaPlayer = MediaPlayer()

  var currentPosition = -1
    set(value) {
      // Update previous audio state
      if (field != -1 && field < audios.size)
        audios[field].setState(AudioState.STOP)
      // Update current audio
      field = value
    }

  var category = -1

  var isPreparing = false

  var audios: MutableList<Audio> = ArrayList()
    set(value) {
      field = value
      currentPosition =
              if (mCurrentAudio == null) -1
              else value.indices.firstOrNull { value[it].equals(mCurrentAudio) } ?: -1
    }

  private var mCurrentAudio: Audio? = null

  var onLockedAudioChoose: ((audio: Audio) -> Unit)? = null

  var onMediaStateChanged: ((audio: Audio) -> Unit)? = null

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

    setEventListeners()

    player.reset()
  }

  fun isCurrentAudio(position: Int): Boolean =
          if (audios.size > 0) audios[position].equals(mCurrentAudio)
          else false

  fun start() {
    stop()
    isPreparing = true
    mCurrentAudio = audios[currentPosition]
    mCurrentAudio?.let {
      it.setState(AudioState.PREPARING)
      it.currentPosition = 0
      onMediaStateChanged?.invoke(it)

      if (it.locked) {
        onLockedAudioChoose?.invoke(it)
      } else {
        prepare(mCurrentAudio!!)
      }
    }
  }

  fun prepared() {
    mCurrentAudio?.let {
      it.currentPosition = 0
      it.duration = player.duration
      it.setState(AudioState.PREPARED)
      onMediaStateChanged?.invoke(it)
    }
  }

  fun play() {
    // Notify current audio state changed
    mCurrentAudio?.let {
      if (it.locked) {
        onLockedAudioChoose?.invoke(it)
      } else {
        it.setState(AudioState.PLAYING)
        it.currentPosition = player.currentPosition
        it.duration = player.duration
        onMediaStateChanged?.invoke(it)

        player.start()
      }
    }
  }

  fun pause() {
    mCurrentAudio?.let {
      if (!it.locked && player.isPlaying) {
        player.pause()

        // Notify previous audio state changed
        mCurrentAudio?.let {
          it.setState(AudioState.PAUSE)
          it.currentPosition = player.currentPosition
          onMediaStateChanged?.invoke(it)
        }
      }
    }
  }

  fun stop() {
    if (player.isPlaying) {
      player.stop()
    }

    // Notify previous audio state changed
    mCurrentAudio?.let {
      it.setState(AudioState.STOP)
      it.currentPosition = 0
      onMediaStateChanged?.invoke(it)
    }

    player.reset()
  }

  fun next() {
    mCurrentAudio?.let {
      it.setState(AudioState.STOP)
      onMediaStateChanged?.invoke(it)
    }

    currentPosition++
    if (currentPosition >= audios.size) {
      currentPosition = 0
    }

    start()
  }

  fun previous() {
    mCurrentAudio?.let {
      it.setState(AudioState.STOP)
      onMediaStateChanged?.invoke(it)
    }

    currentPosition--
    if (currentPosition < 0) {
      currentPosition = audios.size - 1
    }

    start()
  }

  fun isPlaying(): Boolean = player.isPlaying

  fun seekTo(milliSecond: Int) {
    player.seekTo(milliSecond)
  }

  fun unlockAudio() {
    mCurrentAudio?.locked = false

    start()

    Thread({
      mCurrentAudio?.let {
        CacheHelper.updateAudio(it)
      }
    }).start()
  }

  fun setOnPreparedListener(onPreparedListener: MediaPlayer.OnPreparedListener) {
    player.setOnPreparedListener(onPreparedListener)
  }

  fun removeOnPreparedListener() {
    player.setOnPreparedListener(null)
  }

  fun getCurrentAudio(): Audio = mCurrentAudio!!

  fun setOnBufferingUpdateListener(onBufferingUpdateListener: MediaPlayer.OnBufferingUpdateListener) {
    player.setOnBufferingUpdateListener(onBufferingUpdateListener)
  }

  fun dispose() {
    if (player.isPlaying)
      player.stop()
    player.release()
    mCurrentAudio = null
  }

  private fun prepare(audio: Audio) {
    player.setDataSource(audio.url)
    player.prepareAsync()
    isPreparing = true
  }

  private fun setEventListeners() {
    player.setOnCompletionListener {
      // Notify previous audio state changed
      mCurrentAudio?.let {
        it.setState(AudioState.STOP)
        onMediaStateChanged?.invoke(it)
      }
    }
  }
}