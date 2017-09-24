package com.essentd.TDAudio.data

import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.data.remote.RemoteHelper
import com.essentd.TDAudio.utils.OnRemoteResponse

/**
 * Created by dongc on 9/1/2017.
 */
class AppRepository : AppDataSource {
  override fun fetchAudios(callback: OnRemoteResponse<MutableList<Audio?>>) {
    RemoteHelper.fetchAudios(callback)
  }
}