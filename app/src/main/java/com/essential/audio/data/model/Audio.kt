package com.essential.audio.data.model

/**
 * Created by dongc on 8/29/2017.
 */
class Audio(var name: String, var url: String, var isGirlVoice: Boolean, var cover: String) {
  var playing: Boolean = false
    get() = field
    set(value) {
      field = value
    }

  override fun equals(other: Any?): Boolean {
    if (other !is Audio)
      return false
    return name == other.name && url == other.url && isGirlVoice == other.isGirlVoice && cover == other.cover
  }
}