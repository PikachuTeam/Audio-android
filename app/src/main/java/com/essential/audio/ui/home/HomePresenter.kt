package com.essential.audio.ui.home

import com.essential.audio.data.AppDataSource
import com.essential.audio.data.AppRepository
import com.essential.audio.data.model.Audio
import com.essential.audio.utils.MediaController
import com.essential.audio.utils.OnRemoteResponse
import selft.yue.basekotlin.common.BasePresenter

/**
 * Created by dongc on 9/1/2017.
 */
class HomePresenter<V : HomeContract.View>(view: V) : BasePresenter<V>(view), HomeContract.Presenter<V> {

    private val mDataSource: AppDataSource = AppRepository()
    private val mAudios: MutableList<Audio?> = ArrayList()

    override fun loadData() {
        view?.run {
            showLoadingDialog()
            mDataSource.fetchAudios(object : OnRemoteResponse<MutableList<Audio?>> {
                override fun onSuccess(data: MutableList<Audio?>) {
                    dismissLoadingDialog()
                    mAudios.addAll(data)
                    refreshData(data)
                }

                override fun onError(throwable: Throwable) {
                    dismissLoadingDialog()
                    showToast(throwable.message!!)
                }
            })
        }
    }

    override fun playAudio(position: Int) {
        var isNew = false
        mAudios[position]?.let {
            if (MediaController.instance.audios.size == 0 || MediaController.instance.audios[0].url != it.url) {
                val chosenAudios: MutableList<Audio> = ArrayList()
                chosenAudios.add(it)
                MediaController.instance.audios = chosenAudios
                MediaController.instance.currentPosition = 0
                if (MediaController.instance.isPlaying())
                    MediaController.instance.stop()
                isNew = true

                view?.createNotification(it)
            }
            view?.openMediaActivity(it, isNew)
        }
    }

    override fun playAudios() {
    }
}