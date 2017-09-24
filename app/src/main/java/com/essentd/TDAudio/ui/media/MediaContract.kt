package com.essentd.TDAudio.ui.media

import com.essentd.TDAudio.data.model.Audio
import selft.yue.basekotlin.common.BaseContract

/**
 * Created by dongc on 8/31/2017.
 */
interface MediaContract {
  interface View : BaseContract.View {
    fun updateUI(audio: Audio)
  }

  interface Presenter<V : View> : BaseContract.Presenter<V> {
    fun updateData(audioJsonString: String)
  }
}