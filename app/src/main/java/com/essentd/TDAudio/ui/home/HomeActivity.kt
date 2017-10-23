package com.essentd.TDAudio.ui.home

import TDAudio.R
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewTreeObserver
import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.data.model.AudioState
import com.essentd.TDAudio.service.MediaService
import com.essentd.TDAudio.ui.media.MediaActivity
import com.essentd.TDAudio.utils.Constants
import com.essentd.TDAudio.utils.JsonHelper
import com.facebook.common.util.UriUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_home.*
import selft.yue.basekotlin.activity.BaseActivity
import selft.yue.basekotlin.decoration.LinearItemDecoration
import selft.yue.basekotlin.extension.getRealColor


/**
 * Created by dongc on 9/1/2017.
 */
class HomeActivity : BaseActivity(), HomeContract.View {
  private val mPresenter: HomeContract.Presenter<HomeContract.View> = HomePresenter(this)
  private val RC_OVERLAY = 1000

  private val mRvAudios by lazy { rv_audios }
  private val mToolbar by lazy { toolbar }
  private val mBottomSheetMediaPlayer by lazy { bottom_sheet_media_player }
  private val mIvBackground by lazy { iv_background }
  private val mCbBoyVoice by lazy { cb_boy_voice }
  private val mCbGirlVoice by lazy { cb_girl_voice }

  private val mAdapter: AudiosAdapter = AudiosAdapter(this)

  private var mCurrentPosition: Int = -1

  private var mCanExit = false
  private var mCanChangeScreen = true

  private val mMediaControlReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      intent?.run {
        when (action) {
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
            if (hasExtra(Constants.Extra.CURRENT_AUDIO)) {
              var duration = getIntExtra(Constants.Extra.DURATION, 0)
              var progress = getIntExtra(Constants.Extra.PROGRESS, 0)
              val currentAudio = JsonHelper.instance
                      .fromJson(getStringExtra(Constants.Extra.CURRENT_AUDIO), Audio::class.java)

              if (duration < 0)
                duration = 0
              if (progress < 0)
                progress = 0

              mBottomSheetMediaPlayer.setAudioName(currentAudio.name)
              mBottomSheetMediaPlayer.isPlaying = currentAudio.getState() == AudioState.PLAYING ||
                      currentAudio.getState() == AudioState.PREPARED || currentAudio.getState() == AudioState.PREPARING
              mBottomSheetMediaPlayer.setMax(duration)
              mBottomSheetMediaPlayer.setProgress(progress)

              mPresenter.updateAudio(currentAudio)
            }
          }
          Constants.Action.MEDIA_AUDIO_STATE_CHANGED -> {
            val audio = JsonHelper.instance
                    .fromJson(getStringExtra(Constants.Extra.CURRENT_AUDIO), Audio::class.java)
            mPresenter.updateAudio(audio)
            when (audio.getState()) {
              AudioState.PREPARING -> {
                if (mCanChangeScreen && !audio.locked) {
                  mCanChangeScreen = false
                  openMediaActivity()
                }
                mBottomSheetMediaPlayer.setMax(0)
                mBottomSheetMediaPlayer.setProgress(0)

                mBottomSheetMediaPlayer.setAudioName(audio.name)
                mBottomSheetMediaPlayer.isPlaying = false
              }
              AudioState.PREPARED -> {
                mBottomSheetMediaPlayer.setMax(audio.duration)
                mBottomSheetMediaPlayer.setProgress(audio.currentPosition)
              }
              AudioState.PLAYING -> {
                if (mCanChangeScreen) {
                  mCanChangeScreen = false
                  openMediaActivity()
                }

                mBottomSheetMediaPlayer.isPlaying = true
                mBottomSheetMediaPlayer.setProgress(audio.currentPosition)
              }
              AudioState.PAUSE -> {
                mBottomSheetMediaPlayer.isPlaying = false
              }
              AudioState.STOP -> {
                mBottomSheetMediaPlayer.isPlaying = false
              }
            }
          }
          Constants.Action.MEDIA_UPDATE_LIST -> {
            val audiosJsonString = getStringExtra(Constants.Extra.AUDIOS)
            mPresenter.updateAudios(JsonHelper.instance
                    .fromJson(audiosJsonString, object : TypeToken<MutableList<Audio>>() {}.type))
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

    mPresenter.loadData(intent.getBooleanExtra(Constants.Extra.FETCH_REMOTE_DB, false))

    checkAndRequestPermissions(Constants.PermissionRequestCode.SYSTEM_ALERT, arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW))
  }

  override fun onResume() {
    mCanChangeScreen = true

    LocalBroadcastManager.getInstance(this).registerReceiver(mMediaControlReceiver, IntentFilter().apply {
      addAction(Constants.Action.MEDIA_UPDATE_PROGRESS)
      addAction(Constants.Action.MEDIA_GET_CURRENT_STATE)
      addAction(Constants.Action.MEDIA_AUDIO_STATE_CHANGED)
      addAction(Constants.Action.MEDIA_UPDATE_LIST)
    })

    if (mBottomSheetMediaPlayer.visibility == View.VISIBLE) {
      startService(Intent(this, MediaService::class.java).apply {
        action = Constants.Action.MEDIA_UPDATE_LIST
        putExtra(Constants.Extra.UPDATE_CONTROLLER, false)
      })

      startService(Intent(this, MediaService::class.java).apply {
        action = Constants.Action.MEDIA_GET_CURRENT_STATE
      })
    }
    super.onResume()
  }

  override fun onStop() {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(mMediaControlReceiver)
    super.onStop()
  }

  override fun onDestroy() {
    mPresenter.dispose()
    super.onDestroy()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == RC_OVERLAY) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
        playAudio(mCurrentPosition)
      }
    }
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
  }

  override fun updateUI(audio: Audio) {
    mBottomSheetMediaPlayer.setAudioName(audio.name)
  }

  override fun filter(filteredAudios: MutableList<Audio?>) {
    mAdapter.items = filteredAudios
  }

  override fun updateAdapter(position: Int) {
    mAdapter.notifyItemChanged(position)
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

      mCurrentPosition = position

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (!Settings.canDrawOverlays(this)) {
          AlertDialog.Builder(this)
                  .setTitle(getString(R.string.request_permission))
                  .setMessage(getString(R.string.overlay_permission))
                  .setPositiveButton(getString(R.string.agree)) { dialog, _ ->
                    dialog.dismiss()

                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + packageName))
                    startActivityForResult(intent, RC_OVERLAY)
                  }
                  .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                    dialog.dismiss()
                  }
                  .show()
        } else {
          playAudio(position)
        }
      } else {
        playAudio(position)
      }
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
      mCanChangeScreen = false
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

        val temp = "" + R.drawable.app_background

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
      putExtra(Constants.Extra.UPDATE_CONTROLLER, true)
      putExtra(Constants.Extra.AUDIOS, JsonHelper.instance.toJson(mAdapter.items))
    })
  }

  private fun playAudio(position: Int) {
    if (position != -1) {
      if (mBottomSheetMediaPlayer.visibility == View.GONE)
        mBottomSheetMediaPlayer.visibility = View.VISIBLE

      mCanChangeScreen = true
      // Move to media activity
      mPresenter.playAudios(position)
    }
  }
}