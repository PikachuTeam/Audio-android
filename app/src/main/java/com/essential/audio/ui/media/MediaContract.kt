package com.essential.audio.ui.media

import android.content.Intent
import com.essential.audio.data.model.Audio
import selft.yue.basekotlin.common.BaseContract

/**
 * Created by dongc on 8/31/2017.
 */
interface MediaContract {
    interface View : BaseContract.View {
        fun setupUI(audio: Audio)

        fun setupMedia(audioJson: String, chosenPosition: Int, isNew: Boolean)
    }

    interface Presenter<V : View> : BaseContract.Presenter<V> {
        fun loadData(intent: Intent)
    }
}