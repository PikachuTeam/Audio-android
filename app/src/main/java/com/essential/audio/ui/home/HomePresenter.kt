package com.essential.audio.ui.home

import com.essential.audio.data.AppDataSource
import com.essential.audio.data.AppRepository
import com.essential.audio.data.model.Audio
import com.essential.audio.utils.JsonHelper
import com.essential.audio.utils.OnRemoteResponse
import selft.yue.basekotlin.common.BasePresenter

/**
 * Created by dongc on 9/1/2017.
 */
class HomePresenter<V : HomeContract.View>(view: V) : BasePresenter<V>(view), HomeContract.Presenter<V> {

  private val mDataSource: AppDataSource = AppRepository()
  private val mAudios: MutableList<Audio?> = ArrayList()
  private val mFilteredAudios: MutableList<Audio?> = ArrayList()

  override fun loadData() {
    view?.run {
      showLoadingDialog()
      mDataSource.fetchAudios(object : OnRemoteResponse<MutableList<Audio?>> {
        override fun onSuccess(data: MutableList<Audio?>) {
          dismissLoadingDialog()
          mAudios.addAll(data)
          mFilteredAudios.addAll(data)
          refreshData(data)
        }

        override fun onError(throwable: Throwable) {
          dismissLoadingDialog()
          showToast(throwable.message!!)
        }
      })
    }
  }

  override fun playAudios(position: Int) {
    view?.playAudios(mFilteredAudios, position)
  }

  override fun updateData(audioJsonString: String) {
    val audio = JsonHelper.instance.fromJson(audioJsonString, Audio::class.java)
    view?.updateUI(audio)
  }

  override fun filter(voiceType: AudiosAdapter.Voice) {
    if (voiceType == AudiosAdapter.Voice.ALL) {
      mFilteredAudios.clear()
      mFilteredAudios.addAll(mAudios)
    } else {
      mFilteredAudios.clear()
      mFilteredAudios.addAll(mAudios.filter {
        if (it != null) {
          if (voiceType == AudiosAdapter.Voice.BOY)
            !it.isGirlVoice
          else
            it.isGirlVoice
        } else
          false
      })
    }

    view?.filter(mFilteredAudios)
  }
}