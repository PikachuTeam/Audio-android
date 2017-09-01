package com.essential.audio.ui.media

import android.content.Intent
import com.essential.audio.data.model.Audio
import com.essential.audio.utils.Constants
import com.essential.audio.utils.JsonHelper
import selft.yue.basekotlin.common.BasePresenter

/**
 * Created by dongc on 8/31/2017.
 */
class MediaPresenter<V : MediaContract.View>(view: V) : BasePresenter<V>(view), MediaContract.Presenter<V> {
    private var mAudio: Audio? = null

    override fun loadData(intent: Intent) {
        val audioJson = intent.getStringExtra(Constants.Extra.CHOSEN_AUDIO)
        mAudio = JsonHelper.instance.fromJson(audioJson, Audio::class.java)
    }
}