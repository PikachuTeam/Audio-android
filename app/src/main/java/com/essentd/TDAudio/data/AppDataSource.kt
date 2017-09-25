package com.essentd.TDAudio.data

import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.utils.OnRemoteResponse

/**
 * Created by dongc on 9/1/2017.
 */
interface AppDataSource {
  /**
   * Fetch audios from server
   *
   * @param callback: call back to UI when data fetching is finished
   */
  fun fetchAudios(callback: OnRemoteResponse<MutableList<Audio?>>)

  fun getAudios(callback: ((data: MutableList<Audio?>) -> Unit)?)

  fun hasLocalAudios(): Boolean

  fun updateAudios(audios: MutableList<Audio?>, onUpdateFinished: (() -> Unit)?)
}