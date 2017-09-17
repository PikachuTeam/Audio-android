package com.essential.audio.data

import com.essential.audio.data.model.Audio
import com.essential.audio.data.remote.RemoteHelper
import com.essential.audio.utils.OnRemoteResponse

/**
 * Created by dongc on 9/1/2017.
 */
class AppRepository : AppDataSource {
  override fun fetchAudios(callback: OnRemoteResponse<MutableList<Audio?>>) {
    RemoteHelper.fetchAudios(callback)
  }
}