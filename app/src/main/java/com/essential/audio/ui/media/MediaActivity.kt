package com.essential.audio.ui.media

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.AppCompatSeekBar
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.essential.audio.R
import com.essential.audio.data.model.Audio
import com.essential.audio.utils.MediaController
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

    private val mTimeHandler = Handler()
    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            val mediaPlayer = MediaController.instance.player
            if (mediaPlayer.isPlaying)
                updateProgress(mediaPlayer.currentPosition, mediaPlayer.duration)
            mTimeHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL)
        }
    }

    private var mIsPlaying: Boolean = false

    override fun getLayoutResId(): Int = R.layout.activity_media

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()

        mPresenter.loadData(intent)

        setEventListeners()
    }

    override fun onDestroy() {
        mPresenter.dispose()
        mTimeHandler.removeCallbacks(mUpdateTimeTask)
        MediaController.instance.removeOnBufferingUpdateListener()
        MediaController.instance.removeOnPreparedListener()
        MediaController.instance.removeOnCompleteListener()
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

    override fun setupMedia(audio: Audio) {
        if (MediaController.instance.isPlaying()) {
            Log.e(TAG, "Playing")
            val mediaPlayer = MediaController.instance.player

            mIsPlaying = true
            showLoadingProgress(false)
            mButtonPlayPause.setImageResource(R.drawable.ic_pause)

            mSeekBar.max = mediaPlayer.duration
            mTimeHandler.post(mUpdateTimeTask)
        } else {
            Log.e(TAG, "Not Playing")
            showLoadingProgress(true)
            MediaController.instance.prepare(audio.url)
        }
    }

    override fun playAudio(audio: Audio) {
        // Stop update time task
        mTimeHandler.removeCallbacks(mUpdateTimeTask)

        showLoadingProgress(true)
        updateProgress(0, 0)
        mToolbar.title = audio.name

        MediaController.instance.stop()
        MediaController.instance.prepare(audio.url)
    }

    override fun startOver() {
        MediaController.instance.seekTo(0)
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
                MediaController.instance.pause()
                mButtonPlayPause.setImageResource(R.drawable.ic_play)
                mTimeHandler.removeCallbacks(mUpdateTimeTask)
            } else {
                mIsPlaying = true
                MediaController.instance.resume()
                mButtonPlayPause.setImageResource(R.drawable.ic_pause)
                mTimeHandler.post(mUpdateTimeTask)
            }
        }

        btn_next_audio.setOnClickListener {
            mPresenter.nextAudio()
        }

        btn_previous_audio.setOnClickListener {
            mPresenter.previousAudio()
        }

        // Seek bar
        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                p0?.run {
                    MediaController.instance.seekTo(progress)
                }
            }
        })

        // Media events
        MediaController.instance.setOnPreparedListener(MediaPlayer.OnPreparedListener { mediaPlayer ->
            mIsPlaying = true
            mButtonPlayPause.setImageResource(R.drawable.ic_pause)
            showLoadingProgress(false)
            mediaPlayer.seekTo(0)
            mediaPlayer.start()

            // setup seek bar and time
            mSeekBar.max = mediaPlayer.duration
            mTimeHandler.post(mUpdateTimeTask)
        })

        MediaController.instance.setOnBufferingUpdateListener(
                MediaPlayer.OnBufferingUpdateListener { _, percentage -> mSeekBar.secondaryProgress = percentage })

        MediaController.instance.setOnCompleteListener(MediaPlayer.OnCompletionListener {
            mPresenter.nextAudio()
        })
    }

    private fun updateProgress(currentPosition: Int, duration: Int) {
        mSeekBar.progress = currentPosition
        mTvCurrentTime.text = DateTimeUtils.toMediaPlayerTime(currentPosition)
        mTvRestTime.text = DateTimeUtils.toMediaPlayerTime(duration - currentPosition)
    }
}