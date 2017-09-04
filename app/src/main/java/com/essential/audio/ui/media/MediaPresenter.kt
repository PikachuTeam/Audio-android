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

    override fun loadData(intent: Intent) {
        val audioJson = intent.getStringExtra(Constants.Extra.AUDIOS)
        val audios: MutableList<Audio> = JsonHelper.instance.fromJson(audioJson, genericType<MutableList<Audio>>())
        val chosenPosition = intent.getIntExtra(Constants.Extra.CHOSEN_AUDIO, 0)

        view?.setupUI(audios[chosenPosition])
//        view?.setupMedia(audio, intent.getBooleanExtra(Constants.Extra.IS_NEW, false))
    }

    private inline fun <reified T> genericType() = object : TypeToken<T>() {}.type
}