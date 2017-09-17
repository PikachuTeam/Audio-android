package com.essential.audio.ui.media

import com.essential.audio.data.model.Audio
import com.essential.audio.utils.JsonHelper
import com.google.gson.reflect.TypeToken
import selft.yue.basekotlin.common.BasePresenter

/**
 * Created by dongc on 8/31/2017.
 */
class MediaPresenter<V : MediaContract.View>(view: V) : BasePresenter<V>(view), MediaContract.Presenter<V> {
  private val TAG = MediaPresenter::class.java.simpleName

  override fun updateData(audioJsonString: String) {
    val audio = JsonHelper.instance.fromJson(audioJsonString, Audio::class.java)
    view?.updateUI(audio)
  }

  private inline fun <reified T> genericType() = object : TypeToken<T>() {}.type
}