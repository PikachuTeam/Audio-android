package com.essentd.TDAudio.data.remote

import TDAudio.BuildConfig
import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.utils.OnRemoteResponse
import com.essentd.TDAudio.utils.Tables
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
            val isGirlVoice = it.getNumber(Tables.Audio.COLUMN_SPEAKER) == 0
            val url = it.getString(Tables.Audio.COLUMN_FILE)
            val cover = it.getString(Tables.Audio.COLUMN_COVER)
            val unlocked = it.getBoolean(Tables.Audio.COLUMN_UNLOCKED)
            audios.add(Audio(name, url, isGirlVoice, cover, !unlocked))
          }

          callback.onSuccess(audios)
        }
      } else {
        callback.onError(Throwable(ex.message))
      }
    }
  }

  fun fetchTdAudios(callback: OnRemoteResponse<MutableList<Audio?>>) {
    val query: ParseQuery<ParseObject> = ParseQuery.getQuery(BuildConfig.TABLE_NAME)
    query.addAscendingOrder("order").findInBackground { parseObjects, ex ->
      if (ex == null) {
        if (parseObjects.size == 0) {
          callback.onError(Throwable("No results"))
        } else {
          val audios: MutableList<Audio?> = ArrayList()

          parseObjects.forEach {
            val name = it.getString(Tables.TdAudio.COLUMN_NAME)
            val isGirlVoice = it.getNumber(Tables.TdAudio.COLUMN_SPEAKER) == 0
            val url = it.getString(Tables.TdAudio.COLUMN_FILE)
            val cover = it.getString(Tables.TdAudio.COLUMN_COVER)
            val unlocked = it.getBoolean(Tables.Audio.COLUMN_UNLOCKED)
            audios.add(Audio(name, url, isGirlVoice, cover, !unlocked))
          }

          callback.onSuccess(audios)
        }
      } else {
        callback.onError(Throwable(ex.message))
      }
    }
  }
}