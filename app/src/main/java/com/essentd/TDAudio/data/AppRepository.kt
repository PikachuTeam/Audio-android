package com.essentd.TDAudio.data

import TDAudio.BuildConfig
import android.os.AsyncTask
import android.os.AsyncTask.THREAD_POOL_EXECUTOR
import com.essentd.TDAudio.data.local.CacheHelper
import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.data.remote.RemoteHelper
import com.essentd.TDAudio.utils.OnRemoteResponse

/**
 * Created by dongc on 9/1/2017.
 */
class AppRepository : AppDataSource {
  override fun fetchAudios(callback: OnRemoteResponse<MutableList<Audio?>>) {
    if (BuildConfig.DEBUG)
      RemoteHelper.fetchAudios(callback)
    else
      RemoteHelper.fetchTdAudios(callback)
  }

  override fun hasLocalAudios(): Boolean = true

  override fun getAudios(callback: ((data: MutableList<Audio?>) -> Unit)?) {
    object : AsyncTask<Unit, Unit, MutableList<Audio?>>() {
      override fun doInBackground(vararg p0: Unit?): MutableList<Audio?> = CacheHelper.getData()

      override fun onPostExecute(result: MutableList<Audio?>?) {
        result?.let {
          callback?.invoke(it)
        }
      }
    }.executeOnExecutor(THREAD_POOL_EXECUTOR)
  }

  override fun updateAudios(audios: MutableList<Audio?>, onUpdateFinished: (() -> Unit)?) {
    object : AsyncTask<MutableList<Audio?>, Unit, Unit>() {
      override fun doInBackground(vararg p0: MutableList<Audio?>?) {
        p0[0]?.let {
          CacheHelper.updateData(it)
        }
      }

      override fun onPostExecute(result: Unit?) {
        onUpdateFinished?.invoke()
      }
    }.executeOnExecutor(THREAD_POOL_EXECUTOR, audios)
  }
}