package com.essential.audio.data.remote

import com.essential.audio.data.model.Audio
import com.essential.audio.utils.OnRemoteResponse
import com.essential.audio.utils.Tables
import com.parse.ParseObject
import com.parse.ParseQuery

/**
 * Created by dongc on 9/1/2017.
 */
object RemoteHelper {

    fun fetchAudios(callback: OnRemoteResponse<MutableList<Audio?>>) {
        val query: ParseQuery<ParseObject> = ParseQuery.getQuery(Tables.Audio.NAME)
        query.findInBackground { parseObjects, ex ->
            if (ex == null) {
                if (parseObjects.size == 0) {
                    callback.onError(Throwable("No results"))
                } else {
                    val audios: MutableList<Audio?> = ArrayList()

                    parseObjects.forEach {
                        val name = it.getString(Tables.Audio.COLUMN_NAME)
                        val isGirlVoice = it.getBoolean(Tables.Audio.COLUMN_IS_GIRL_VOICE)
                        val url = it.getParseFile(Tables.Audio.COLUMN_FILE).url
                        audios.add(Audio(name, url, isGirlVoice))
                    }

                    callback.onSuccess(audios)
                }
            } else {
                callback.onError(Throwable(ex.message))
            }
        }
    }
}