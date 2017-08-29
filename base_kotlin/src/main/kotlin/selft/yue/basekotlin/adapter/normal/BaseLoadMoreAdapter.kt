package selft.yue.basekotlin.adapter.normal

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by dongc on 8/26/2017.
 */
abstract class BaseLoadMoreAdapter<E, VH : RecyclerView.ViewHolder>(recyclerView: RecyclerView, onLoadMore: () -> Unit) : BaseAdapter<E, RecyclerView.ViewHolder>() {
    private val VIEW_TYPE_LOAD_MORE = 1
    private val VIEW_TYPE_NORMAL = 2
    private val DEFAULT_VISIBLE_THRESHOLD = 7

    private val mRecyclerView: RecyclerView = recyclerView
    var visibleThreshold = DEFAULT_VISIBLE_THRESHOLD
        get() = visibleThreshold
        set(value) {
            field = value
        }
    private var mLastVisibleItem: Int = 0
    private var mTotalItemCount: Int = 0
    private var mIsLoading: Boolean = false

    init {
        if (mRecyclerView.layoutManager is LinearLayoutManager) {
            val layoutManager = mRecyclerView.layoutManager as LinearLayoutManager
            mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    mTotalItemCount = layoutManager.itemCount
                    mLastVisibleItem = layoutManager.findLastVisibleItemPosition()

                    if (!mIsLoading && mTotalItemCount < mLastVisibleItem + visibleThreshold) {
                        onLoadMore()
                        mIsLoading = true
                    }
                }
            })
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] == null) VIEW_TYPE_LOAD_MORE else VIEW_TYPE_NORMAL
    }

    fun addLoadMoreItem(recyclerView: RecyclerView) {
        items.add(null)
        notifyItemInserted(items.size - 1)
    }

    fun removeLoadMoreItem() {
        mIsLoading = false
        val indexOfLoadMoreItem = items.indices.firstOrNull { items[it] == null } ?: -1
        if (indexOfLoadMoreItem != -1) {
            items.removeAt(indexOfLoadMoreItem)
            notifyItemRemoved(indexOfLoadMoreItem)
        }
    }
}