package com.essentd.TDAudio.data.local

import com.essentd.TDAudio.data.model.Audio
import io.realm.Realm

/**
 * Created by dong on 24/09/2017.
 */
class RealmHelper {
  private val mRealm = Realm.getDefaultInstance()
//
//  fun getRealm(): Realm = Realm.getDefaultInstance()

  fun getAllAudios() {
    mRealm.executeTransactionAsync(Realm.Transaction { realm: Realm? ->

    })
  }

  fun insertAudios() {

  }

  fun updateAudio(audio: Audio) {

  }
}