package com.essentd.TDAudio.ui.home

import com.essentd.TDAudio.data.model.Audio
import io.realm.RealmList
import selft.yue.basekotlin.common.BaseContract

/**
 * Created by dongc on 9/1/2017.
 */
interface HomeContract {
  interface View : BaseContract.View {
    fun openMediaActivity()

    fun playAudios(audioUrls: MutableList<String>, chosenPosition: Int)

    fun updateUI(audio: Audio)

    fun filter(filteredAudios: RealmList<Audio?>)

    fun updateAdapter(position: Int)

    fun refreshData(data: RealmList<Audio?>)
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

    fun updateAudios(audios: RealmList<Audio>)
  }
}