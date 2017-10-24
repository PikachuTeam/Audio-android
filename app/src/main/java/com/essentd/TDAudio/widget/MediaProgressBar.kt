package com.essentd.TDAudio.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import TDAudio.R
import selft.yue.basekotlin.extension.convertDpToPixel

/**
 * Created by dong on 16/09/2017.
 */
class MediaProgressBar : RelativeLayout {
  private lateinit var mTvCurrentTime: TextView
  private lateinit var mDurationProgress: ProgressBar

  var max: Int = 0
    get() = field
    set(value) {
      field = value
      mDurationProgress.max = value
    }

  var progress: Int = 0
    get() = field
    set(value) {
      field = value
      mDurationProgress.progress = value

      if (width == 0) {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
          override fun onGlobalLayout() {
            if (Build.VERSION.SDK_INT < 16)
              viewTreeObserver.removeGlobalOnLayoutListener(this)
            else
              viewTreeObserver.removeOnGlobalLayoutListener(this)

            var x = (value * width).toFloat() / max - mTvCurrentTime.width / 2
            if (x < 0)
              x = 0f
            else if (x + mTvCurrentTime.width > width)
              x = (width - mTvCurrentTime.width).toFloat()
            mTvCurrentTime.x = x
          }
        })
      } else {
        var x = (value * width).toFloat() / max - mTvCurrentTime.width / 2
        if (x < 0)
          x = 0f
        else if (x + mTvCurrentTime.width > width)
          x = (width - mTvCurrentTime.width).toFloat()
        mTvCurrentTime.x = x
      }

      mTvCurrentTime.text = DateTimeUtils.toMediaPlayerTime(value)
    }

  var progressBarHeight: Int = 0
    set(value) {
      field = value
      val params = mDurationProgress.layoutParams as LayoutParams
      params.height = value
    }

  constructor(context: Context) : this(context, null)

  constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

  constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
    init(context, attributes)
  }

  private fun init(context: Context, attributes: AttributeSet?) {
    View.inflate(context, R.layout.media_progress_bar, this)

    mTvCurrentTime = findViewById(R.id.tv_current_time)
    mDurationProgress = findViewById(R.id.progress_duration)

    if (attributes != null) {
      val typedArray = context.obtainStyledAttributes(attributes, R.styleable.MediaProgressBar)
      max = typedArray.getInteger(R.styleable.MediaProgressBar_maxProgress, 100)
      progress = typedArray.getInteger(R.styleable.MediaProgressBar_progress, 0)
      progressBarHeight = context.convertDpToPixel(typedArray.getDimension(R.styleable.MediaProgressBar_progressHeight, 0f))

      typedArray.recycle()
    }
  }
}