package com.essential.audio.ui.home

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import com.essential.audio.R
import com.essential.audio.data.model.Audio
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

    private val mBottomSheetBehavior by lazy { BottomSheetBehavior.from(mBottomSheetMediaPlayer) }
    private val mAdapter: AudiosAdapter = AudiosAdapter()

    override fun getLayoutResId(): Int = R.layout.activity_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupToolbar()
        setUpRecyclerView()
        setEventListeners()

        mPresenter.loadData()
    }

    override fun onDestroy() {
        mPresenter.dispose()
        super.onDestroy()
    }

    override fun refreshData(data: MutableList<Audio?>) {
        if (data.isNotEmpty()) {
            mAdapter.items = data
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
        mRvAudios.addItemDecoration(LinearItemDecoration(3))
        mRvAudios.adapter = mAdapter
    }

    private fun setEventListeners() {
        mAdapter.onMainItemClick = { audio ->
            audio?.run {
                showToast(name)
                if (mBottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED ||
                        mBottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }
}