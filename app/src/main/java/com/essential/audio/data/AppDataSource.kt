package com.essential.audio.data

import com.essential.audio.data.model.Audio
import com.essential.audio.utils.OnRemoteResponse

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
}