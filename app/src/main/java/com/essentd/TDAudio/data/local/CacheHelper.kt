package com.essentd.TDAudio.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.utils.Constants

/**
 * Created by dong on 24/09/2017.
 */
class CacheHelper constructor(context: Context) {
  val mSharePreferences: SharedPreferences = context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, MODE_PRIVATE)

//  fun saveData(audios: MutableList<Audio>)

//  fun loadData(): MutableList<Audio> {
//
//  }
}