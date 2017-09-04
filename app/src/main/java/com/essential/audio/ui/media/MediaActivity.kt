package com.essential.audio.ui.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
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
import com.essential.audio.service.MediaService
import com.essential.audio.utils.Constants
import com.essential.audio.widget.DateTimeUtils
import kotlinx.android.synthetic.main.activity_media.*
import selft.yue.basekotlin.activity.BaseActivity
import selft.yue.basekotlin.extension.getRealColor

/**
 * Created by dongc on 8/31/2017.
 */
class MediaActivity : BaseActivity(), MediaContract.View {
    private val TAG = MediaPlayer::class.java.simpleName
    private val UPDATE_PROGRESS_INTERVAL = 1000L

    private val mPresenter: MediaContract.Presenter<MediaContract.View> = MediaPresenter(this)

    private val mToolbar: Toolbar by lazy { toolbar }
    private val mTvCurrentTime: TextView by lazy { tv_current_time }
    private val mTvRestTime: TextView by lazy { tv_rest_time }
    private val mSeekBar: AppCompatSeekBar by lazy { seek_bar_duration }
    private val mLoadingProgress: ProgressBar by lazy { loading_progress }
    private val mButtonPlayPause: ImageView by lazy { btn_play_pause }

    private var mCurrentPosition = 0
    private var mDuration = 0

    private val mTimeHandler = Handler()
    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            mCurrentPosition += 1000
            if (mCurrentPosition <= mDuration) {
                updateProgress(mCurrentPosition, mDuration)
                mTimeHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL)
            }
        }
    }

    private var mIsPlaying: Boolean = false

    private val mMediaControlReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.run {
                when (action) {
                    Constants.Action.MEDIA_PREPARING -> {
                        showLoadingProgress(true)
                        mToolbar.title = intent.getStringExtra(Constants.Extra.AUDIO_NAME)
                        mTimeHandler.removeCallbacks(mUpdateTimeTask)

                        mIsPlaying = false
                    }
                    Constants.Action.MEDIA_PREPARED -> {
                        mButtonPlayPause.setImageResource(R.drawable.ic_pause)
                        showLoadingProgress(false)
                        mCurrentPosition = 0
                        mDuration = intent.getIntExtra(Constants.Extra.DURATION, 0)

                        // setup seek bar and time
                        mSeekBar.max = mDuration
                        mTimeHandler.post(mUpdateTimeTask)

                        mIsPlaying = true
                    }
                    Constants.Action.MEDIA_PLAY -> {
                        mButtonPlayPause.setImageResource(R.drawable.ic_pause)
                        mTimeHandler.post(mUpdateTimeTask)
                        mIsPlaying = true
                    }
                    Constants.Action.MEDIA_PAUSE, Constants.Action.MEDIA_FINISH_PLAYING -> {
                        if (mButtonPlayPause.visibility == View.INVISIBLE) {
                            showLoadingProgress(false)
                        }
                        mButtonPlayPause.setImageResource(R.drawable.ic_play)
                        mTimeHandler.removeCallbacks(mUpdateTimeTask)
                        mIsPlaying = false
                    }
                    Constants.Action.MEDIA_NEXT -> {
                        if (!mIsPlaying) {
                            mTimeHandler.post(mUpdateTimeTask)
                            mButtonPlayPause.setImageResource(R.drawable.ic_pause)
                            mIsPlaying = true
                        }
                        mCurrentPosition = 0
                    }
                    Constants.Action.MEDIA_PREVIOUS -> {
                        if (!mIsPlaying) {
                            mTimeHandler.post(mUpdateTimeTask)
                            mButtonPlayPause.setImageResource(R.drawable.ic_pause)

                            mIsPlaying = true
                        }
                        mCurrentPosition = 0
                    }
                }
            }
        }
    }

    override fun getLayoutResId(): Int = R.layout.activity_media

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()

        mPresenter.loadData(intent)

        setEventListeners()
    }

    override fun onResume() {
        registerReceiver(mMediaControlReceiver, IntentFilter().apply {
            addAction(Constants.Action.MEDIA_PREPARING)
            addAction(Constants.Action.MEDIA_PREPARED)
            addAction(Constants.Action.MEDIA_PLAY)
            addAction(Constants.Action.MEDIA_PAUSE)
            addAction(Constants.Action.MEDIA_NEXT)
            addAction(Constants.Action.MEDIA_PREVIOUS)
            addAction(Constants.Action.MEDIA_FINISH_PLAYING)
        })
        super.onResume()
    }

    override fun onStop() {
        unregisterReceiver(mMediaControlReceiver)
        super.onStop()
    }

    override fun onDestroy() {
        mPresenter.dispose()
        mTimeHandler.removeCallbacks(mUpdateTimeTask)
//        MediaController.instance.removeOnBufferingUpdateListener()
        super.onDestroy()
    }

    override fun setupUI(audio: Audio) {
        mToolbar.title = audio.name
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setupMedia(audios: MutableList<Audio>, chosenPosition: Int, isNew: Boolean) {
        if (isNew) {
            showLoadingProgress(true)
            startService(Intent(this, MediaService::class.java).apply {
                //TODO: Not done
                action = Constants.Action.MEDIA_START
            })
        } else {
//            val mediaPlayer = MediaController.instance.player

            mIsPlaying = true
            showLoadingProgress(false)
            mButtonPlayPause.setImageResource(R.drawable.ic_pause)

//            mSeekBar.max = mediaPlayer.duration
            mTimeHandler.post(mUpdateTimeTask)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(mToolbar)
        supportActionBar?.run {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back)
        }

        mToolbar.setTitleTextColor(getRealColor(R.color.white))
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
                mIsPlaying = false
                mButtonPlayPause.setImageResource(R.drawable.ic_play)
                mTimeHandler.removeCallbacks(mUpdateTimeTask)

                startMediaService(Constants.Action.MEDIA_PAUSE)
            } else {
                mIsPlaying = true
                mButtonPlayPause.setImageResource(R.drawable.ic_pause)
                mTimeHandler.post(mUpdateTimeTask)

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
                    mCurrentPosition = progress
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