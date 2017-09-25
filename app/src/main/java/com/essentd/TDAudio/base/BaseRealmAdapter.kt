package com.essentd.TDAudio.base

import android.support.v7.widget.RecyclerView
import io.realm.RealmList
import io.realm.RealmObject

/**
 * Created by dong on 9/25/17.
 */
abstract class BaseRealmAdapter<E : RealmObject, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
  private var mItems: RealmList<E?> = RealmList()
  var items: RealmList<E?>
    get() = this.mItems
    set(value) {
      mItems.clear()
      mItems.addAll(value)
      notifyDataSetChanged()
    }

  override fun getItemCount(): Int = mItems.size

  open fun addItems(items: RealmList<E>) {
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