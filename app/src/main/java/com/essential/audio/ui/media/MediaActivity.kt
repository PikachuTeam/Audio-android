package com.essential.audio.ui.media

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.essential.audio.R
import kotlinx.android.synthetic.main.activity_media.*
import selft.yue.basekotlin.activity.BaseActivity
import selft.yue.basekotlin.extension.getRealColor

/**
 * Created by dongc on 8/31/2017.
 */
class MediaActivity : BaseActivity(), MediaContract.View {
    private val mPresenter: MediaContract.Presenter<MediaContract.View> = MediaPresenter(this)

    private val mToolbar: Toolbar by lazy { toolbar }
    private val mTvCurrentTime: TextView by lazy { tv_current_time }
    private val mTvRestTime: TextView by lazy { tv_rest_time }

    override fun getLayoutResId(): Int = R.layout.activity_media

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPresenter.loadData(intent)

        setupToolbar()
    }

    override fun onDestroy() {
        mPresenter.dispose()
        super.onDestroy()
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

    private fun aloha(){

    }
}