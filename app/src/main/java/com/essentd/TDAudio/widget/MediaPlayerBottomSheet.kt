package com.essentd.TDAudio.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import TDAudio.R

/**
 * Created by dongc on 9/1/2017.
 */
class MediaPlayerBottomSheet : ConstraintLayout {
  private lateinit var mMediaProgress: MediaProgressBar
  private lateinit var mBtnPlayPause: ImageView
  private lateinit var mTvAudioName: TextView
  private lateinit var mTvAudioDuration: TextView

  var onFunctionClickListener: ((view: View) -> Unit)? = null
    get() = field
    set(value) {
      field = value
    }

  var isPlaying: Boolean = false
    get() = field
    set(value) {
      field = value
      mBtnPlayPause.setImageResource(if (value) R.drawable.ic_pause else R.drawable.ic_play)
    }

  constructor(context: Context) : this(context, null)

  constructor(context: Context, attributes: AttributeSet?) : this(context, attributes, 0)

  constructor(context: Context, attributes: AttributeSet?, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
    init(context)
  }

  private fun init(context: Context) {
    View.inflate(context, R.layout.item_media_player, this)

    mMediaProgress = findViewById(R.id.progress_media)
    mBtnPlayPause = findViewById(R.id.btn_play_pause)
    mTvAudioName = findViewById(R.id.tv_audio_name)
    mTvAudioDuration = findViewById(R.id.tv_audio_duration)

    mBtnPlayPause.setOnClickListener {
      onFunctionClickListener?.invoke(it)
    }
    findViewById<View>(R.id.btn_next_audio).setOnClickListener {
      onFunctionClickListener?.invoke(it)
    }
    findViewById<View>(R.id.btn_previous_audio).setOnClickListener {
      onFunctionClickListener?.invoke(it)
    }
    findViewById<View>(R.id.item_container).setOnClickListener {
      onFunctionClickListener?.invoke(it)
    }
  }

  fun setAudioName(audioName: String) {
    mTvAudioName.text = audioName
  }

  fun setMax(max: Int) {
    mMediaProgress.max = max
  }

  fun getMax(): Int = mMediaProgress.max

  fun setProgress(progress: Int) {
    mMediaProgress.progress = progress
    mTvAudioDuration.text = DateTimeUtils.toMediaPlayerTime(mMediaProgress.max - progress)
  }
}