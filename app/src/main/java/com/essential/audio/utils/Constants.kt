package com.essential.audio.utils

/**
 * Created by dongc on 8/31/2017.
 */
object Constants {
  val APP_SHARED_PREFERENCES = Constants::class.java.`package`.name

  object Pref {
    val CURRENT_BACKGROUND_POSITION = "CURRENT_BACKGROUND_POSITION"
  }

  object Extra {
    val AUDIOS = "AUDIOS"
    val CHOSEN_AUDIO = "CHOSEN_AUDIO"
    val DURATION = "DURATION"
    val AUDIO_NAME = "AUDIO_NAME"
    val PROGRESS = "PROGRESS"
    val IS_PLAYING = "IS_PLAYING"
    val IS_PREPARING = "IS_PREPARING"
    val CURRENT_AUDIO = "CURRENT_AUDIO"
    val CURRENT_POSITION = "CURRENT_POSITION"
    val UPDATE_CONTROLLER = "UPDATE_CONTROLLER"
  }

  object Action {
    val MEDIA_START = "MEDIA_START"
    val MEDIA_PLAY = "MEDIA_PLAY"
    val MEDIA_PAUSE = "MEDIA_PAUSE"
    val MEDIA_NEXT = "MEDIA_NEXT"
    val MEDIA_PREVIOUS = "MEDIA_PREVIOUS"
    val MEDIA_PREPARING = "MEDIA_PREPARING"
    val MEDIA_PREPARED = "MEDIA_PREPARED"
    val MEDIA_SEEK_TO = "MEDIA_SEEK_TO"
    val MEDIA_BUFFERING = "MEDIA_BUFFERING"
    val MEDIA_UPDATE_PROGRESS = "MEDIA_UPDATE_PROGRESS"
    val MEDIA_AUDIO_COMPLETED = "MEDIA_AUDIO_COMPLETED"
    val MEDIA_FINISH_PLAYING = "MEDIA_FINISH_PLAYING"
    val MEDIA_GET_CURRENT_STATE = "MEDIA_GET_CURRENT_STATE"
    val MEDIA_UPDATE_LIST = "MEDIA_UPDATE_LIST"
    val MEDIA_AUDIO_STATE_CHANGED = "MEDIA_AUDIO_STATE_CHANGED"
  }

  object FirebaseConfig {
    val AUDIO_VERSION = "AUDIO_VERSION"
    val IMAGES = "IMAGES"
    val PREVIEW_VERSION = "PREVIEW_VERSION"
    val PREVIEW_IMAGES = "PREVIEW_IMAGES"
  }
}