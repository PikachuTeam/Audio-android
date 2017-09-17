package com.essential.audio

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import com.parse.Parse
import io.fabric.sdk.android.Fabric

/**
 * Created by dongc on 8/29/2017.
 */
class AudioApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    Parse.initialize(Parse.Configuration.Builder(this)
            .applicationId(getString(R.string.app_id))
            .server(getString(R.string.server_address))
            .clientKey(getString(R.string.client_key))
            .build())

    Fresco.initialize(this)

    val fabric = Fabric.Builder(this)
            .kits(Crashlytics())
            .debuggable(true)
            .build()
    Fabric.with(fabric)
  }
}