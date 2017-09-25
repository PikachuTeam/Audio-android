package com.essentd.TDAudio.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.essentd.TDAudio.utils.Constants

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
}