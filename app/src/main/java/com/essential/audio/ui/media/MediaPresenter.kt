package com.essential.audio.ui.media

import android.content.Intent
import com.essential.audio.data.model.Audio
import com.essential.audio.utils.Constants
import com.essential.audio.utils.JsonHelper
import com.google.gson.reflect.TypeToken
import selft.yue.basekotlin.common.BasePresenter

/**
 * Created by dongc on 8/31/2017.
 */
class MediaPresenter<V : MediaContract.View>(view: V) : BasePresenter<V>(view), MediaContract.Presenter<V> {
    private val TAG = MediaPresenter::class.java.simpleName

    private val mAudios: MutableList<Audio> = ArrayList()
    private var mCurrentPosition = -1

    override fun loadData(intent: Intent) {
        mCurrentPosition = intent.getIntExtra(Constants.Extra.CHOSEN_AUDIO, 0)
        val audioJson = intent.getStringExtra(Constants.Extra.AUDIOS)
        mAudios.addAll(JsonHelper.instance.fromJson(audioJson, genericType<MutableList<Audio>>()))
        view?.setupUI(mAudios[mCurrentPosition])
        view?.setupMedia(mAudios[mCurrentPosition])
    }

    override fun nextAudio() {
        mCurrentPosition++
        if (mCurrentPosition >= mAudios.size) {
            mCurrentPosition = mAudios.size - 1
            view?.startOver()
            return
        }
        view?.playAudio(mAudios[mCurrentPosition])
    }

    override fun previousAudio() {
        mCurrentPosition--
        if (mCurrentPosition < 0) {
            mCurrentPosition = 0
            view?.startOver()
            return
        }
        view?.playAudio(mAudios[mCurrentPosition])
    }

    private inline fun <reified T> genericType() = object : TypeToken<T>() {}.type
}