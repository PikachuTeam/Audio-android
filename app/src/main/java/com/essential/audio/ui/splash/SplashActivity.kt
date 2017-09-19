package com.essential.audio.ui.splash

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.ViewTreeObserver
import com.essential.audio.BuildConfig
import com.essential.audio.R
import com.essential.audio.ui.home.HomeActivity
import com.essential.audio.utils.BackgroundController
import com.essential.audio.utils.Constants
import com.essential.audio.utils.MediaController
import com.facebook.common.util.UriUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_splash.*
import selft.yue.basekotlin.activity.BaseActivity
import selft.yue.basekotlin.extension.isNetworkAvailable

/**
 * Created by dongc on 9/2/2017.
 */
class SplashActivity : BaseActivity() {
  private val CACHE_EXPIRATION = 10000L

  private val mIvBackground: SimpleDraweeView by lazy { iv_background }

  private val mOnFirebaseFetchConfigComplete = OnCompleteListener<Void> { task ->
    if (task.isSuccessful) {
      FirebaseRemoteConfig.getInstance().activateFetched()

      val audioVersion = FirebaseRemoteConfig.getInstance().getLong(Constants.FirebaseConfig.AUDIO_VERSION)
      val previewVersion = FirebaseRemoteConfig.getInstance().getLong(Constants.FirebaseConfig.PREVIEW_VERSION)
      val packageInfo = packageManager.getPackageInfo(packageName, 0)
      val images: String =
              if (previewVersion == 0L || previewVersion != packageInfo.versionCode.toLong())
                FirebaseRemoteConfig.getInstance().getString(Constants.FirebaseConfig.IMAGES)
              else
                FirebaseRemoteConfig.getInstance().getString(Constants.FirebaseConfig.PREVIEW_IMAGES)
      FirebaseRemoteConfig.getInstance().activateFetched()
      BackgroundController.instance.setBackgroundImages(images)

      startActivity(Intent(this, HomeActivity::class.java))
      finish()
    } else {
      AlertDialog.Builder(this)
              .setMessage(
                      if (isNetworkAvailable()) getString(R.string.fail_to_fetch_config)
                      else getString(R.string.network_error)
              )
              .setPositiveButton(getString(R.string.try_again)) { dialog, _ ->
                dialog.dismiss()
                fetchFirebaseConfig()
              }
              .setNegativeButton(getString(R.string.exit_app)) { _, _ ->
                finish()
              }
              .create().show()
    }
  }

  override fun getLayoutResId(): Int = R.layout.activity_splash

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setBackground()

    BackgroundController.instance.init(this)

    fetchFirebaseConfig()
  }

  private fun setBackground() {
    mIvBackground.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
      override fun onGlobalLayout() {
        if (Build.VERSION.SDK_INT < 16)
          mIvBackground.viewTreeObserver.removeGlobalOnLayoutListener(this)
        else
          mIvBackground.viewTreeObserver.removeOnGlobalLayoutListener(this)

        val temp = "" + R.drawable.app_background_2

        val imageUri = Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(temp)
                .build()

        val imageRequest = ImageRequestBuilder
                .newBuilderWithSource(imageUri)
                .setResizeOptions(ResizeOptions(mIvBackground.width, mIvBackground.height))
                .build()

        mIvBackground.controller = Fresco.newDraweeControllerBuilder()
                .setOldController(mIvBackground.controller)
                .setImageRequest(imageRequest)
                .build()
      }
    })
  }

  private fun fetchFirebaseConfig() {
    FirebaseRemoteConfig.getInstance().fetch(CACHE_EXPIRATION)
            .addOnCompleteListener(this, mOnFirebaseFetchConfigComplete)
  }
}