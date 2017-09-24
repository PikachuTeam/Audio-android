package com.essentd.TDAudio.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.view.ViewTreeObserver
import TDAudio.R
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.drawable.ScalingUtils
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder

/**
 * Created by dong on 17/09/2017.
 */
class BackgroundController private constructor() {
  private val UPDATE_INTERVAL = 15000L
  private val ANIMATION_DURATION = 3000L

  private val mBackgroundImages: MutableList<String> = ArrayList()

  private lateinit var mIvBackground1: SimpleDraweeView
  private lateinit var mIvBackground2: SimpleDraweeView

  private lateinit var mSharedPref: SharedPreferences
  private lateinit var mContext: Context

  private var mCurrentPosition = -1
  private var mWidth = 0
  private var mHeight = 0

  private var mFirstShowed = false

  private val backgroundTimer: CountDownTimer = object : CountDownTimer(Long.MAX_VALUE, UPDATE_INTERVAL) {
    override fun onTick(p0: Long) {
      mFirstShowed = if (mFirstShowed) {
        show2()
        false
      } else {
        show1()
        true
      }
    }

    override fun onFinish() {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
  }

  companion object {
    val instance = BackgroundController()
  }

  fun init(context: Context) {
    mContext = context
    mSharedPref = context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE)
    mCurrentPosition = if (mSharedPref.contains(Constants.Pref.CURRENT_BACKGROUND_POSITION)) {
      mSharedPref.getInt(Constants.Pref.CURRENT_BACKGROUND_POSITION, -1)
    } else {
      -1
    }
  }

  fun setBackgroundImages(imagesString: String) {
    mBackgroundImages += imagesString.split(',')
  }

  fun stop() {
    backgroundTimer.cancel()
    mSharedPref.edit().putInt(Constants.Pref.CURRENT_BACKGROUND_POSITION, mCurrentPosition - 1).apply()
  }

  fun playBackgrounds(ivBackground1: SimpleDraweeView, ivBackground2: SimpleDraweeView) {
    mCurrentPosition = if (mSharedPref.contains(Constants.Pref.CURRENT_BACKGROUND_POSITION)) {
      mSharedPref.getInt(Constants.Pref.CURRENT_BACKGROUND_POSITION, -1)
    } else {
      -1
    }

    mFirstShowed = false

    mIvBackground1 = ivBackground1
    mIvBackground2 = ivBackground2

    mIvBackground1.hierarchy.setPlaceholderImage(
            ContextCompat.getDrawable(mContext, R.drawable.app_background_2),
            ScalingUtils.ScaleType.CENTER_CROP
    )
    mIvBackground2.hierarchy.setPlaceholderImage(
            ContextCompat.getDrawable(mContext, R.drawable.app_background_2),
            ScalingUtils.ScaleType.CENTER_CROP
    )

    if (mIvBackground2.width == 0) {
      mIvBackground2.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
          if (Build.VERSION.SDK_INT < 16)
            mIvBackground2.viewTreeObserver.removeGlobalOnLayoutListener(this)
          else
            mIvBackground2.viewTreeObserver.removeOnGlobalLayoutListener(this)

          mWidth = mIvBackground2.width
          mHeight = mIvBackground2.height
          play()
        }
      })
    } else {
      mWidth = mIvBackground2.width
      mHeight = mIvBackground2.height
      play()
    }
  }

  private fun play() {
    mCurrentPosition++
    if (mCurrentPosition >= mBackgroundImages.size)
      mCurrentPosition = 0

    // Set image for background
    var imageUrl = mBackgroundImages[mCurrentPosition]
    setImageBackground(mIvBackground1, imageUrl)

    backgroundTimer.start()
  }

  private fun show1() {
    mIvBackground1.animate().alpha(1f).setDuration(ANIMATION_DURATION).setListener(null)
    mIvBackground2.animate().alpha(0f).setDuration(ANIMATION_DURATION).setListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator?) {
        mCurrentPosition++
        if (mCurrentPosition >= mBackgroundImages.size)
          mCurrentPosition = 0

        setImageBackground(mIvBackground2, mBackgroundImages[mCurrentPosition])
      }
    })
  }

  private fun show2() {
    mIvBackground1.animate().alpha(0f).setDuration(ANIMATION_DURATION).setListener(object : AnimatorListenerAdapter() {
      override fun onAnimationEnd(animation: Animator?) {
        mCurrentPosition++
        if (mCurrentPosition >= mBackgroundImages.size)
          mCurrentPosition = 0

        setImageBackground(mIvBackground1, mBackgroundImages[mCurrentPosition])
      }
    })
    mIvBackground2.animate().alpha(1f).setDuration(ANIMATION_DURATION).setListener(null)
  }

  private fun setImageBackground(ivBackground: SimpleDraweeView, imageUrl: String) {
    val imageUri = Uri.parse(imageUrl.trim())
    val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(imageUri)
            .setResizeOptions(ResizeOptions(mWidth, mHeight))
            .build()
    ivBackground.controller = Fresco.newDraweeControllerBuilder()
            .setOldController(ivBackground.controller)
            .setImageRequest(imageRequest)
            .build()
  }
}