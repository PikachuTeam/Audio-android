package com.essential.audio.data.model

/**
 * Created by dongc on 8/29/2017.
 */
class Audio(var name: String, var url: String, var isGirlVoice: Boolean, var cover: String, var locked: Boolean) {
  var state: AudioState = AudioState.STOP

  var currentPosition = 0
  var duration: Int = 0

  override fun equals(other: Any?): Boolean {
    if (other == null)
      return false
    if (other !is Audio)
      return false
    return name == other.name && url == other.url && isGirlVoice == other.isGirlVoice && cover == other.cover
  }

  fun copyState(other: Audio?) {
    other?.let {
      locked = it.locked
      state = it.state
    }
  }
}