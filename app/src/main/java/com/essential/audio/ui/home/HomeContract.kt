package com.essential.audio.ui.home

import com.essential.audio.data.model.Audio
import selft.yue.basekotlin.common.BaseContract
import selft.yue.basekotlin.util.HasNormalRecyclerView

/**
 * Created by dongc on 9/1/2017.
 */
interface HomeContract {
    interface View : BaseContract.View, HasNormalRecyclerView<Audio?> {
        fun openMediaActivity(audio: Audio, isNew: Boolean)

        fun reOpenMediaActivity()

        fun createNotification(audio: Audio)
    }

    interface Presenter<V : View> : BaseContract.Presenter<V> {
        /**
         * Load data from remote or local
         */
        fun loadData()

        fun playAudio(position: Int)

        fun playAudios()
    }
}