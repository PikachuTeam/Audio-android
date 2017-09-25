package com.essentd.TDAudio.ui.home

import android.util.Log
import com.essentd.TDAudio.data.AppDataSource
import com.essentd.TDAudio.data.AppRepository
import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.data.model.AudioState
import com.essentd.TDAudio.utils.JsonHelper
import com.essentd.TDAudio.utils.OnRemoteResponse
import selft.yue.basekotlin.common.BasePresenter

/**
 * Created by dongc on 9/1/2017.
 */
class HomePresenter<V : HomeContract.View>(view: V) : BasePresenter<V>(view), HomeContract.Presenter<V> {
  private val mDataSource: AppDataSource = AppRepository()
  private val mAudios: MutableList<Audio?> = ArrayList()
  private val mFilteredAudios: MutableList<Audio?> = ArrayList()

  override fun loadData(loadRemote: Boolean) {
    view?.run {
      showLoadingDialog()
      if (!loadRemote) {
        getLocalAudios()
      } else {
        mDataSource.fetchAudios(object : OnRemoteResponse<MutableList<Audio?>> {
          override fun onSuccess(data: MutableList<Audio?>) {
            mDataSource.updateAudios(data) {
              getLocalAudios()
            }
          }

          override fun onError(throwable: Throwable) {
            dismissLoadingDialog()
            showToast(throwable.message!!)
          }
        })
      }
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

  override fun updateAudio(audio: Audio) {
    val realIndex = mAudios.indices.firstOrNull { mAudios[it]?.equals(audio) ?: false } ?: -1
    if (realIndex != -1) {
      mAudios[realIndex]?.copyState(audio)

      // Find the last played audio
      val previousAudio = mAudios.firstOrNull {
        it?.getState() == AudioState.PLAYING ||
                it?.getState() == AudioState.PREPARING ||
                it?.getState() == AudioState.PREPARED
      }

      // Update previous audio
      previousAudio?.let {
        val foundIndex = mFilteredAudios.indices
                .firstOrNull { mFilteredAudios[it]?.equals(previousAudio) ?: false } ?: -1
        if (foundIndex != -1)
          view?.updateAdapter(foundIndex)
      }

      // Update current audio
      val filteredIndex = mFilteredAudios.indices
              .firstOrNull { mFilteredAudios[it]?.equals(audio) ?: false } ?: -1
      if (filteredIndex != -1)
        view?.updateAdapter(filteredIndex)
    }
  }

  override fun updateAudios(audios: MutableList<Audio>) {
    // Update current list
    for (i in audios.indices) {
      mFilteredAudios[i]?.let {
        if (it.equals(audios[i]) && (it.locked != audios[i].locked || it.state != audios[i].state)) {
          it.copyState(audios[i])
          view?.updateAdapter(i)
        }
      }
    }

    // Update total list
    mFilteredAudios.forEach({ audio ->
      val foundAudio = mAudios.firstOrNull { it?.equals(audio) ?: false }
      foundAudio?.copyState(audio ?: null)
    })
  }

  private fun getLocalAudios() {
    mDataSource.getAudios { data ->
      mAudios.clear()
      mAudios.addAll(data)
      view?.dismissLoadingDialog()
      view?.refreshData(data)
    }
  }
}