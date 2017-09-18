package com.essential.audio.utils

import android.os.CountDownTimer
import android.widget.ImageView
import com.facebook.imagepipeline.request.ImageRequest

/**
 * Created by dong on 17/09/2017.
 */
class BackgroundController private constructor() {
  private val UPDATE_INTERVAL = 10000L

  val backgroundImages: MutableList<String> = ArrayList()

  private lateinit var mIvBackground1: ImageView
  private lateinit var mIvBackground2: ImageView

  private var mCurrentPosition = -1

  private val backgroundTimer: CountDownTimer = object : CountDownTimer(Long.MAX_VALUE, UPDATE_INTERVAL) {
    override fun onTick(p0: Long) {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFinish() {
      TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
  }

  companion object {
    val instance = BackgroundController()
  }

  fun setBackgroundImages(imagesString: String) {
    backgroundImages += imagesString.split(',')
  }

  fun play() {
    mCurrentPosition++
    if (mCurrentPosition >= backgroundImages.size)
      mCurrentPosition = 0
    backgroundTimer.start()
  }

  fun stop() {
    backgroundTimer.cancel()
  }

  fun setBackgrounds(ivBackground1: ImageView, ivBackground2: ImageView) {
    mIvBackground1 = ivBackground1
    mIvBackground2 = ivBackground2
    play()
  }

  private fun show1() {

  }

  private fun show2() {

  }
}