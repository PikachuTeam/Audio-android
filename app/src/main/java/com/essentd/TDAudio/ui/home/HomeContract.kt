package com.essentd.TDAudio.ui.home

import com.essentd.TDAudio.data.model.Audio
import selft.yue.basekotlin.common.BaseContract

/**
 * Created by dongc on 9/1/2017.
 */
interface HomeContract {
  interface View : BaseContract.View {
    fun openMediaActivity()

    fun playAudios(audios: MutableList<Audio?>, chosenPosition: Int)

    fun updateUI(audio: Audio)

    fun filter(filteredAudios: MutableList<Audio?>)

    fun updateAdapter(position: Int)

    fun refreshData(data: MutableList<Audio?>)
  }

  interface Presenter<V : View> : BaseContract.Presenter<V> {
    /**
     * Load data from remote or local
     */
    fun loadData(loadRemote: Boolean)

    fun playAudios(position: Int)

    fun updateData(audioJsonString: String)

    fun filter(voiceType: AudiosAdapter.Voice)

    fun updateAudio(audio: Audio)

    fun updateAudios(audios: MutableList<Audio>)
  }
}