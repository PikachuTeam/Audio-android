package com.essentd.TDAudio.data.model

import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

/**
 * Created by dongc on 8/29/2017.
 */
open class Audio() : RealmObject() {
  open var name: String = ""
  @PrimaryKey
  open var url: String = ""
  open var isGirlVoice: Boolean = false
  open var cover: String = ""
  open var locked: Boolean = false
  @Ignore
  var state: String = AudioState.STOP.toString()
  @Ignore
  var currentPosition = 0
  @Ignore
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


}