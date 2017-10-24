package com.essentd.TDAudio.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.utils.Constants
import com.essentd.TDAudio.utils.JsonHelper
import com.google.gson.reflect.TypeToken

/**
 * Created by dong on 24/09/2017.
 */
object CacheHelper {
  private lateinit var mSharePreferences: SharedPreferences

  fun init(context: Context) {
    mSharePreferences = context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, MODE_PRIVATE)
  }

  fun saveDbVersion(version: Long) {
    mSharePreferences.edit().putLong(Constants.Pref.CURRENT_DATABASE_VERSION, version).apply()
  }

  fun getDbVersion(): Long =
          if (!mSharePreferences.contains(Constants.Pref.CURRENT_DATABASE_VERSION)) 0
          else mSharePreferences.getLong(Constants.Pref.CURRENT_DATABASE_VERSION, 0)

  fun updateData(audios: MutableList<Audio?>) {
    val currentAudiosJson: String =
            if (mSharePreferences.contains(Constants.Pref.AUDIOS)) mSharePreferences.getString(Constants.Pref.AUDIOS, "")
            else ""
    val currentAudios: MutableList<Audio?> =
            if (currentAudiosJson.isEmpty()) ArrayList()
            else JsonHelper.instance.fromJson(currentAudiosJson, object : TypeToken<MutableList<Audio?>>() {}.type)

    if (currentAudios.size == 0)
      currentAudios.addAll(0, audios)
    else {
      audios.forEach { audio ->
        audio?.let {
          if (!currentAudios.contains(audio)) {
            currentAudios.add(audio)
          }
        }
      }
    }
    mSharePreferences.edit().putString(Constants.Pref.AUDIOS, JsonHelper.instance.toJson(currentAudios)).apply()
  }

  fun getData(): MutableList<Audio?> {
    val currentAudiosJson: String =
            if (mSharePreferences.contains(Constants.Pref.AUDIOS)) mSharePreferences.getString(Constants.Pref.AUDIOS, "")
            else ""

    val currentAudios: MutableList<Audio?> =
            if (currentAudiosJson.isEmpty()) ArrayList()
            else JsonHelper.instance.fromJson(currentAudiosJson, object : TypeToken<MutableList<Audio?>>() {}.type)

    currentAudios.forEach {
      it?.resetState()
    }

    return currentAudios
  }

  fun updateAudio(audio: Audio) {
    val currentAudiosJson: String =
            if (mSharePreferences.contains(Constants.Pref.AUDIOS)) mSharePreferences.getString(Constants.Pref.AUDIOS, "")
            else ""
    val currentAudios: MutableList<Audio?> =
            if (currentAudiosJson.isEmpty()) ArrayList()
            else JsonHelper.instance.fromJson(currentAudiosJson, object : TypeToken<MutableList<Audio?>>() {}.type)

    val foundIndex = currentAudios.indices.firstOrNull { currentAudios[it]?.equals(audio) ?: false } ?: -1

    if (foundIndex != -1) {
      currentAudios[foundIndex]?.copyState(audio)
    }

    mSharePreferences.edit().putString(Constants.Pref.AUDIOS, JsonHelper.instance.toJson(currentAudios)).apply()
  }
}