package com.essential.audio.utils

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import com.essential.audio.data.model.Audio
import com.essential.audio.data.model.AudioState

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

  var isPreparing = false
    get() = field
    set(value) {
      field = value
    }

  var audios: MutableList<Audio> = ArrayList()
    get() = field
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
    mCurrentAudio = audios[currentPosition]
    mCurrentAudio?.let {
      it.state = AudioState.PREPARING
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
      it.state = AudioState.PREPARED
      onMediaStateChanged?.invoke(it)
    }
  }

  fun play() {
    // Notify current audio state changed
    mCurrentAudio?.let {
      if (it.locked) {
        onLockedAudioChoose?.invoke(it)
      } else {
        it.state = AudioState.PLAYING
        it.currentPosition = player.currentPosition
        it.duration = player.duration
        onMediaStateChanged?.invoke(it)
      }
    }

    player.start()
  }

  fun pause() {
    if (player.isPlaying) {
      player.pause()

      // Notify previous audio state changed
      mCurrentAudio?.let {
        it.state = AudioState.PAUSE
        it.currentPosition = player.currentPosition
        onMediaStateChanged?.invoke(it)
      }
    }
  }

  fun stop() {
    if (player.isPlaying) {
      player.stop()

      // Notify previous audio state changed
      mCurrentAudio?.let {
        it.state = AudioState.STOP
        it.currentPosition = 0
        onMediaStateChanged?.invoke(it)
      }
    }
    player.reset()
  }

  fun next() {
    mCurrentAudio?.let {
      it.state = AudioState.STOP
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
      it.state = AudioState.STOP
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

  fun unlockAudio(audio: Audio) {
    currentPosition = audios.indices.first { audios[it].equals(audio) }
    audios[currentPosition].locked = false
    start()
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
        it.state = AudioState.STOP
        onMediaStateChanged?.invoke(it)
      }
    }
  }
}