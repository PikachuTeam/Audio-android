package com.essential.audio.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import com.essential.audio.R
import com.essential.audio.data.model.Audio
import com.essential.audio.service.MediaService
import com.essential.audio.ui.media.MediaActivity
import com.essential.audio.utils.Constants
import com.essential.audio.utils.JsonHelper
import com.facebook.common.util.UriUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import kotlinx.android.synthetic.main.activity_home.*
import selft.yue.basekotlin.activity.BaseActivity
import selft.yue.basekotlin.decoration.LinearItemDecoration
import selft.yue.basekotlin.extension.getRealColor

/**
 * Created by dongc on 9/1/2017.
 */
class HomeActivity : BaseActivity(), HomeContract.View {
  private val mPresenter: HomeContract.Presenter<HomeContract.View> = HomePresenter(this)

  private val mRvAudios by lazy { rv_audios }
  private val mToolbar by lazy { toolbar }
  private val mBottomSheetMediaPlayer by lazy { bottom_sheet_media_player }
  private val mIvBackground by lazy { iv_background }
  private val mCbBoyVoice by lazy { cb_boy_voice }
  private val mCbGirlVoice by lazy { cb_girl_voice }

  private var mCurrentPosition = -1

  private val mAdapter: AudiosAdapter = AudiosAdapter(this)

  private var mCanExit = false

  private val mMediaControlReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      intent?.run {
        when (action) {
          Constants.Action.MEDIA_PREPARING -> {
            mBottomSheetMediaPlayer.setAudioName(getStringExtra(Constants.Extra.AUDIO_NAME))
            mBottomSheetMediaPlayer.isPlaying = false
          }
          Constants.Action.MEDIA_PREPARED -> {
            mBottomSheetMediaPlayer.isPlaying = true
          }
          Constants.Action.MEDIA_PLAY -> {
            mBottomSheetMediaPlayer.isPlaying = true
          }
          Constants.Action.MEDIA_PAUSE, Constants.Action.MEDIA_FINISH_PLAYING -> {
            mBottomSheetMediaPlayer.isPlaying = false
          }
          Constants.Action.MEDIA_NEXT -> {
            startMediaService(Constants.Action.MEDIA_GET_CURRENT_STATE)
          }
          Constants.Action.MEDIA_PREVIOUS -> {
            startMediaService(Constants.Action.MEDIA_GET_CURRENT_STATE)
          }
          Constants.Action.MEDIA_UPDATE_PROGRESS -> {
            val duration = getIntExtra(Constants.Extra.DURATION, 0)
            val currentPosition = getIntExtra(Constants.Extra.PROGRESS, 0)

            if (!mBottomSheetMediaPlayer.isPlaying)
              mBottomSheetMediaPlayer.isPlaying = true

            if (mBottomSheetMediaPlayer.getMax() != duration)
              mBottomSheetMediaPlayer.setMax(duration)
            mBottomSheetMediaPlayer.setProgress(currentPosition)
          }
          Constants.Action.MEDIA_GET_CURRENT_STATE -> {
            val duration = getIntExtra(Constants.Extra.DURATION, 0)
            val currentPosition = getIntExtra(Constants.Extra.PROGRESS, 0)
            val audioName = getStringExtra(Constants.Extra.AUDIO_NAME)
            mBottomSheetMediaPlayer.setAudioName(audioName)
            mBottomSheetMediaPlayer.isPlaying = getBooleanExtra(Constants.Extra.IS_PLAYING, false)
            mBottomSheetMediaPlayer.setMax(duration)
            mBottomSheetMediaPlayer.setProgress(currentPosition)
          }
        }
      }
    }
  }

  override fun getLayoutResId(): Int = R.layout.activity_home

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setupToolbar()
    setUpRecyclerView()
    setEventListeners()
    setBackground()

    mPresenter.loadData()
  }

  override fun onResume() {
    LocalBroadcastManager.getInstance(this).registerReceiver(mMediaControlReceiver, IntentFilter().apply {
      addAction(Constants.Action.MEDIA_PREPARING)
      addAction(Constants.Action.MEDIA_PREPARED)
      addAction(Constants.Action.MEDIA_PLAY)
      addAction(Constants.Action.MEDIA_PAUSE)
      addAction(Constants.Action.MEDIA_NEXT)
      addAction(Constants.Action.MEDIA_PREVIOUS)
      addAction(Constants.Action.MEDIA_FINISH_PLAYING)
      addAction(Constants.Action.MEDIA_UPDATE_PROGRESS)
      addAction(Constants.Action.MEDIA_GET_CURRENT_STATE)
    })

    if (mBottomSheetMediaPlayer.visibility == View.VISIBLE)
      startService(Intent(this, MediaService::class.java).apply {
        action = Constants.Action.MEDIA_GET_CURRENT_STATE
      })

    super.onResume()
  }

  override fun onDestroy() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMediaControlReceiver)
    mPresenter.dispose()
    super.onDestroy()
  }

  override fun onBackPressed() {
    if (mCanExit) {
      stopService(Intent(this, MediaService::class.java))
      finish()
    } else {
      mCanExit = true
      showToast(R.string.press_again_to_exit)
      Handler().postDelayed({
        mCanExit = false
      }, 3000L)
    }
  }

  override fun refreshData(data: MutableList<Audio?>) {
    if (data.isNotEmpty()) {
      mAdapter.items = data
      if (!mCbBoyVoice.isChecked && !mCbGirlVoice.isChecked) {
        mPresenter.filter(AudiosAdapter.Voice.ALL)
      } else {
        mPresenter.filter(
                if (mCbBoyVoice.isChecked) AudiosAdapter.Voice.BOY
                else AudiosAdapter.Voice.GIRL
        )
      }
    }
  }

  override fun openMediaActivity() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMediaControlReceiver)
    startActivity(Intent(this@HomeActivity, MediaActivity::class.java))
  }

  override fun playAudios(audios: MutableList<Audio?>, chosenPosition: Int) {
    startService(Intent(this, MediaService::class.java).apply {
      action = Constants.Action.MEDIA_START
      putExtra(Constants.Extra.AUDIOS, JsonHelper.instance.toJson(audios))
      putExtra(Constants.Extra.CHOSEN_AUDIO, chosenPosition)
    })

    openMediaActivity()
  }

  override fun updateUI(audio: Audio) {
    mBottomSheetMediaPlayer.setAudioName(audio.name)
  }

  override fun filter(filteredAudios: MutableList<Audio?>) {
    mAdapter.items = filteredAudios
  }

  private fun setupToolbar() {
    setSupportActionBar(mToolbar)
    supportActionBar?.run {
      setDisplayShowTitleEnabled(false)
      setDisplayHomeAsUpEnabled(false)
    }

    mToolbar.setTitleTextColor(getRealColor(R.color.white))
  }

  private fun setUpRecyclerView() {
    mRvAudios.layoutManager = LinearLayoutManager(this)
    mRvAudios.addItemDecoration(LinearItemDecoration(25, 30))
    mRvAudios.adapter = mAdapter
  }

  private fun setEventListeners() {
    mAdapter.onMainItemClick = { position ->
      if (mBottomSheetMediaPlayer.visibility == View.GONE)
        mBottomSheetMediaPlayer.visibility = View.VISIBLE

      mCurrentPosition = position
      // Move to media activity
      mPresenter.playAudios(position)
    }

    mCbGirlVoice.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        if (mCbBoyVoice.isChecked) {
          mPresenter.filter(AudiosAdapter.Voice.ALL)
        } else {
          mPresenter.filter(AudiosAdapter.Voice.GIRL)
        }
      } else {
        if (!mCbBoyVoice.isChecked) {
          mPresenter.filter(AudiosAdapter.Voice.ALL)
        } else {
          mPresenter.filter(AudiosAdapter.Voice.BOY)
        }
      }
      updateAudiosList()
    }

    mCbBoyVoice.setOnCheckedChangeListener { _, isChecked ->
      if (isChecked) {
        if (mCbGirlVoice.isChecked) {
          mPresenter.filter(AudiosAdapter.Voice.ALL)
        } else {
          mPresenter.filter(AudiosAdapter.Voice.BOY)
        }
      } else {
        if (!mCbGirlVoice.isChecked) {
          mPresenter.filter(AudiosAdapter.Voice.ALL)
        } else {
          mPresenter.filter(AudiosAdapter.Voice.GIRL)
        }
      }
      updateAudiosList()
    }

    mBottomSheetMediaPlayer.onFunctionClickListener = { view ->
      if (view.id == R.id.btn_play_pause) {
        if (mBottomSheetMediaPlayer.isPlaying) {
          startMediaService(Constants.Action.MEDIA_PAUSE)
        } else {
          startMediaService(Constants.Action.MEDIA_PLAY)
        }
      } else if (view.id == R.id.btn_next_audio) {
        startMediaService(Constants.Action.MEDIA_NEXT)
      } else if (view.id == R.id.btn_previous_audio) {
        startMediaService(Constants.Action.MEDIA_PREVIOUS)
      } else if (view.id == R.id.item_container) {
        openMediaActivity()
      }
    }
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

  private fun startMediaService(action: String) {
    startService(Intent(this, MediaService::class.java).apply {
      this.action = action
    })
  }

  private fun updateAudiosList() {
    startService(Intent(this@HomeActivity, MediaService::class.java).apply {
      action = Constants.Action.MEDIA_UPDATE_LIST
      putExtra(Constants.Extra.AUDIOS, JsonHelper.instance.toJson(mAdapter.items))
    })
  }
}