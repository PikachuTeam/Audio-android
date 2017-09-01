package com.essential.audio.ui.home

import com.essential.audio.data.AppDataSource
import com.essential.audio.data.AppRepository
import com.essential.audio.data.model.Audio
import com.essential.audio.utils.OnRemoteResponse
import selft.yue.basekotlin.common.BasePresenter

/**
 * Created by dongc on 9/1/2017.
 */
class HomePresenter<V : HomeContract.View>(view: V) : BasePresenter<V>(view), HomeContract.Presenter<V> {
    private val mDataSource: AppDataSource = AppRepository()

    override fun loadData() {
        view?.run {
            showLoadingDialog()
            mDataSource.fetchAudios(object : OnRemoteResponse<MutableList<Audio?>> {
                override fun onSuccess(data: MutableList<Audio?>) {
                    dismissLoadingDialog()
                    refreshData(data)
                }

                override fun onError(throwable: Throwable) {
                    dismissLoadingDialog()
                    showToast(throwable.message!!)
                }
            })
        }
    }
}