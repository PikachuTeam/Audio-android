package com.essential.audio

import android.app.Application
import com.parse.Parse

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
    }
}