package com.essential.audio.ui.home

import com.essential.audio.data.model.Audio
import selft.yue.basekotlin.common.BaseContract
import selft.yue.basekotlin.util.HasNormalRecyclerView

/**
 * Created by dongc on 9/1/2017.
 */
interface HomeContract {
  interface View : BaseContract.View, HasNormalRecyclerView<Audio?> {
    fun openMediaActivity()

    fun playAudios(audios: MutableList<Audio?>, chosenPosition: Int)

    fun updateUI(audio: Audio)

    fun filter(filteredAudios: MutableList<Audio?>)

    fun updateAdapter(position: Int)
  }

  interface Presenter<V : View> : BaseContract.Presenter<V> {
    /**
     * Load data from remote or local
     */
    fun loadData()

    fun playAudios(position: Int)

    fun updateData(audioJsonString: String)

    fun filter(voiceType: AudiosAdapter.Voice)

    fun updateAudio(audio: Audio)
  }
}