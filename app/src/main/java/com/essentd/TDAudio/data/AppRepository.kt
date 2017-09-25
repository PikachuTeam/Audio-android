package com.essentd.TDAudio.data

import TDAudio.BuildConfig
import com.essentd.TDAudio.data.local.LocalHelper
import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.data.remote.RemoteHelper
import com.essentd.TDAudio.utils.OnRemoteResponse
import io.realm.RealmList

/**
 * Created by dongc on 9/1/2017.
 */
class AppRepository : AppDataSource {
  private val mLocalHelper: LocalHelper = LocalHelper()

  override fun fetchAudios(callback: OnRemoteResponse<MutableList<Audio?>>) {
    if (BuildConfig.DEBUG)
      RemoteHelper.fetchAudios(callback)
    else
      RemoteHelper.fetchTdAudios(callback)
  }

  override fun hasLocalAudios(): Boolean = mLocalHelper.hasAnyAudios()

  override fun getAudios(callback: (data: RealmList<Audio?>) -> Unit) {
    mLocalHelper.getAudios(callback)
  }

  override fun updateAudios(audios: MutableList<Audio?>, onUpdateFinished: () -> Unit) {
    mLocalHelper.updateAudios(audios, onUpdateFinished)
  }
}