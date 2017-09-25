package com.essentd.TDAudio.data.local

import android.util.Log
import com.essentd.TDAudio.data.model.Audio
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmList
import io.realm.RealmResults

/**
 * Created by dong on 24/09/2017.
 */
class LocalHelper {
  private val mUiRealm = Realm.getDefaultInstance()

  fun getAudios(callback: (data: RealmList<Audio?>) -> Unit) {
    val audios = mUiRealm.where(Audio::class.java).findAllAsync()
    audios.addChangeListener(object : RealmChangeListener<RealmResults<Audio>> {
      override fun onChange(realmResults: RealmResults<Audio>) {
        audios.removeChangeListener(this)
        callback.invoke(RealmList<Audio?>().apply {
          addAll(realmResults)
        })
      }
    })
  }

  fun updateAudios(audios: MutableList<Audio?>, onUpdateFinished: () -> Unit) {
    mUiRealm.executeTransactionAsync(Realm.Transaction { realm ->
      Log.e("LocalHelper", "Do job: " + Thread.currentThread().name)
      audios.forEach {
        it?.apply {
          var audio = realm.where(Audio::class.java).equalTo("url", url).findFirst()
          if (audio == null) {
            Log.e("LocalHelper", "audio null")
            audio = Audio(name, url, isGirlVoice, cover, locked)
            realm.copyToRealm(audio)
          } else {
            Log.e("LocalHelper", "audio not null")
          }
        }
      }
      realm.close()
    }, Realm.Transaction.OnSuccess {
      Log.e("LocalHelper", "Success: " + Thread.currentThread().name)
      onUpdateFinished.invoke()
    })
  }

  fun hasAnyAudios(): Boolean = mUiRealm.where(Audio::class.java).findFirst() != null
}