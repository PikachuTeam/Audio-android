package com.essentd.TDAudio.data.model

/**
 * Created by dongc on 8/29/2017.
 */
open class Audio() {
  var name: String = ""
  var url: String = ""
  var isGirlVoice: Boolean = false
  var cover: String = ""
  var locked: Boolean = false
  var state: String = AudioState.STOP.toString()
  var currentPosition = 0
  var duration: Int = 0

  constructor(name: String, url: String, isGirlVoice: Boolean, cover: String, locked: Boolean) : this() {
    this.name = name
    this.url = url
    this.isGirlVoice = isGirlVoice
    this.cover = cover
    this.locked = locked
  }

  override fun equals(other: Any?): Boolean {
    if (other == null)
      return false
    if (other !is Audio)
      return false
    return name == other.name && url == other.url && isGirlVoice == other.isGirlVoice && cover == other.cover
  }

  override fun hashCode(): Int {
    var result = name.hashCode()
    result = 31 * result + url.hashCode()
    result = 31 * result + isGirlVoice.hashCode()
    result = 31 * result + cover.hashCode()
    return result
  }

  fun copyState(other: Audio?) {
    other?.let {
      locked = it.locked
      state = it.state
    }
  }

  fun getState(): AudioState = AudioState.valueOf(state)

  fun setState(state: AudioState) {
    this.state = state.toString()
  }

  fun resetState() {
    currentPosition = 0
    state = AudioState.STOP.toString()
  }
}