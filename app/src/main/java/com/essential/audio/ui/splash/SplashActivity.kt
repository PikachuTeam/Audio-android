package com.essential.audio.ui.splash

import android.content.Intent
import android.os.Bundle
import com.essential.audio.R
import com.essential.audio.ui.home.HomeActivity
import com.essential.audio.utils.MediaController
import selft.yue.basekotlin.activity.BaseActivity

/**
 * Created by dongc on 9/2/2017.
 */
class SplashActivity : BaseActivity() {
    override fun getLayoutResId(): Int = R.layout.activity_splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, HomeActivity::class.java))
    }
}