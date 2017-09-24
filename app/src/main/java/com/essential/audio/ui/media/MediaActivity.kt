package com.essential.audio.ui.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.essential.audio.R
import com.essential.audio.data.model.Audio
import com.essential.audio.data.model.AudioState
import com.essential.audio.service.MediaService
import com.essential.audio.utils.BackgroundController
import com.essential.audio.utils.Constants
import com.essential.audio.utils.JsonHelper
import com.essential.audio.widget.DateTimeUtils
import kotlinx.android.synthetic.main.activity_media.*
import selft.yue.basekotlin.activity.BaseActivity

/**
 * Created by dongc on 8/31/2017.
 */
class MediaActivity : BaseActivity(), MediaContract.View {
  private val TAG = MediaPlayer::class.java.simpleName

  private val mPresenter: MediaContract.Presenter<MediaContract.View> = MediaPresenter(this)

  private val mToolbar: Toolbar by lazy { toolbar }
  private val mTvCurrentTime: TextView by lazy { tv_current_time }
  private val mTvRestTime: TextView by lazy { tv_rest_time }
  private val mSeekBar: AppCompatSeekBar by lazy { seek_bar_duration }
  private val mLoadingProgress: ProgressBar by lazy { loading_progress }
  private val mButtonPlayPause: ImageView by lazy { btn_play_pause }
  private val mTvTitle: TextView by lazy { tv_title }

  private var mIsPlaying: Boolean = false

  private val mMediaControlReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      intent?.run {
        when (action) {
          Constants.Action.MEDIA_UPDATE_PROGRESS -> {
            val duration = getIntExtra(Constants.Extra.DURATION, 0)
            val currentPosition = getIntExtra(Constants.Extra.PROGRESS, 0)
            if (mSeekBar.max != duration)
              mSeekBar.max = duration
            updateProgress(currentPosition, duration)
          }
          Constants.Action.MEDIA_GET_CURRENT_STATE -> {
            var duration = getIntExtra(Constants.Extra.DURATION, 0)
            var currentPosition = getIntExtra(Constants.Extra.PROGRESS, 0)
            val audio = JsonHelper.instance.fromJson(getStringExtra(Constants.Extra.CURRENT_AUDIO), Audio::class.java)

            if (duration < 0)
              duration = 0
            if (currentPosition < 0)
              currentPosition = 0

            if (audio.state == AudioState.PREPARING) {
              showLoadingProgress(true)
            } else {
              showLoadingProgress(false)

              mButtonPlayPause.setImageResource(
                      if (audio.state == AudioState.PLAYING ||
                              audio.state == AudioState.PREPARING ||
                              audio.state == AudioState.PREPARED) {
                        mIsPlaying = true
                        R.drawable.ic_pause
                      } else R.drawable.ic_play
              )
            }

            if (mSeekBar.max != duration)
              mSeekBar.max = duration
            updateProgress(currentPosition, duration)
            mTvTitle.text = audio.name
          }
          Constants.Action.MEDIA_AUDIO_STATE_CHANGED -> {
            val audio = JsonHelper.instance.fromJson(getStringExtra(Constants.Extra.CURRENT_AUDIO), Audio::class.java)
            when (audio.state) {
              AudioState.PREPARING -> {
                if (!audio.locked) {
                  showLoadingProgress(true)
                  mIsPlaying = true
                }
                mTvTitle.text = audio.name
                updateProgress(audio.currentPosition, audio.duration)
              }
              AudioState.PREPARED -> {
                mButtonPlayPause.setImageResource(R.drawable.ic_pause)
                showLoadingProgress(false)
                mIsPlaying = true
              }
              AudioState.PLAYING -> {
                mButtonPlayPause.setImageResource(R.drawable.ic_pause)
                mIsPlaying = true
              }
              AudioState.PAUSE -> {
                mButtonPlayPause.setImageResource(R.drawable.ic_play)
                mIsPlaying = false
              }
              AudioState.STOP -> {
                mButtonPlayPause.setImageResource(R.drawable.ic_play)
                mIsPlaying = false
              }
            }
          }
        }
      }
    }
  }

  override fun getLayoutResId(): Int = R.layout.activity_media

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setupToolbar()
    setEventListeners()
  }

  override fun onResume() {
    LocalBroadcastManager.getInstance(this).registerReceiver(mMediaControlReceiver, IntentFilter().apply {
      addAction(Constants.Action.MEDIA_UPDATE_PROGRESS)
      addAction(Constants.Action.MEDIA_GET_CURRENT_STATE)
      addAction(Constants.Action.MEDIA_AUDIO_STATE_CHANGED)
    })

    startMediaService(Constants.Action.MEDIA_GET_CURRENT_STATE)

    BackgroundController.instance.playBackgrounds(iv_background_1, iv_background_2)
    super.onResume()
  }

  override fun onStop() {
    BackgroundController.instance.stop()
    super.onStop()
  }

  override fun onDestroy() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMediaControlReceiver)
    mPresenter.dispose()
    super.onDestroy()
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    if (item?.itemId == android.R.id.home) {
      onBackPressed()
    }
    return super.onOptionsItemSelected(item)
  }

  override fun updateUI(audio: Audio) {
    mTvTitle.text = audio.name
  }

  private fun setupToolbar() {
    setSupportActionBar(mToolbar)
    supportActionBar?.run {
      setDisplayShowTitleEnabled(false)
    }
    mToolbar.setNavigationIcon(R.drawable.ic_close)
    mToolbar.setNavigationOnClickListener {
      onBackPressed()
    }
  }

  private fun showLoadingProgress(showed: Boolean) {
    if (showed) {
      mButtonPlayPause.visibility = View.INVISIBLE
      mLoadingProgress.visibility = View.VISIBLE
      mLoadingProgress.isIndeterminate = true
    } else {
      mButtonPlayPause.visibility = View.VISIBLE
      mLoadingProgress.isIndeterminate = false
      mLoadingProgress.visibility = View.INVISIBLE
    }
  }

  private fun setEventListeners() {
    // Button events
    mButtonPlayPause.setOnClickListener {
      if (mIsPlaying) {
        startMediaService(Constants.Action.MEDIA_PAUSE)
      } else {
        startMediaService(Constants.Action.MEDIA_PLAY)
      }
    }

    btn_next_audio.setOnClickListener {
      startMediaService(Constants.Action.MEDIA_NEXT)
    }

    btn_previous_audio.setOnClickListener {
      startMediaService(Constants.Action.MEDIA_PREVIOUS)
    }

    // Seek bar
    mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
      }

      override fun onStartTrackingTouch(p0: SeekBar?) {
      }

      override fun onStopTrackingTouch(p0: SeekBar?) {
        p0?.run {
          startService(Intent(this@MediaActivity, MediaService::class.java).apply {
            action = Constants.Action.MEDIA_SEEK_TO
            putExtra(Constants.Extra.PROGRESS, p0.progress)
          })
        }
      }
    })
  }

  private fun updateProgress(currentPosition: Int, duration: Int) {
    mSeekBar.progress = currentPosition
    mTvCurrentTime.text = DateTimeUtils.toMediaPlayerTime(currentPosition)
    mTvRestTime.text = DateTimeUtils.toMediaPlayerTime(duration - currentPosition)
  }

  private fun startMediaService(action: String) {
    startService(Intent(this, MediaService::class.java).apply {
      this.action = action
    })
  }
}