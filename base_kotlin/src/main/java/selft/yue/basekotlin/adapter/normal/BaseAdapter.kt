package selft.yue.basekotlin.adapter.normal

import android.support.v7.widget.RecyclerView

/**
 * Created by dongc on 8/26/2017.
 */
abstract class BaseAdapter<E, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private var mItems: MutableList<E?> = ArrayList()
    var items: MutableList<E?>
        get() = this.mItems
        set(value) {
            mItems.clear()
            mItems.addAll(value)
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return mItems.size
    }

    open fun addItems(items: MutableList<E>) {
        val previousSize = mItems.size
        mItems.addAll(items)
        notifyItemRangeChanged(previousSize, items.size)
    }

    open fun removeItem(item: E) {
        val indexOfFoundItem = findItem(item)
        if (indexOfFoundItem != -1) {
            mItems.removeAt(indexOfFoundItem)
            notifyItemRemoved(mItems.size)
        }
    }

    abstract fun findItem(item: E): Int
}