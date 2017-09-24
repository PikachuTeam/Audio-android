package com.essentd.TDAudio.ui.home

import TDAudio.R
import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import com.essentd.TDAudio.data.model.Audio
import com.essentd.TDAudio.data.model.AudioState
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import selft.yue.basekotlin.adapter.normal.BaseAdapter

/**
 * Created by dongc on 9/1/2017.
 */
class AudiosAdapter(context: Context) : BaseAdapter<Audio, AudiosAdapter.ItemHolder>() {
  enum class Voice {
    ALL, GIRL, BOY
  }

  private val mContext = context
  private var mImageWidth = 0
  private var mImageHeight = 0

  var onMainItemClick: ((position: Int) -> Unit)? = null

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder =
          ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false))

  override fun onBindViewHolder(holder: ItemHolder, position: Int) {
    val audio = items[position]
    audio?.run {
      holder.tvAudioName.text = name

      if (isGirlVoice) {
        holder.tvGender.text = mContext.getString(R.string.girl)
        holder.ivGender.setImageResource(R.drawable.ic_girl_underwear)
      } else {
        holder.tvGender.text = mContext.getString(R.string.boy)
        holder.ivGender.setImageResource(R.drawable.ic_boy_underwear)
      }

      holder.ivLocked.visibility = if (locked) View.VISIBLE else View.GONE
      holder.stateArea.visibility =
              if (getState() == AudioState.PLAYING || getState() == AudioState.PREPARING || getState() == AudioState.PREPARED) View.VISIBLE
              else View.GONE

      if (mImageWidth == 0 || mImageHeight == 0) {
        if (holder.ivCover.measuredWidth == 0 || holder.ivCover.measuredHeight == 0) {
          holder.ivCover.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
              holder.ivCover.viewTreeObserver.removeOnPreDrawListener(this)

              mImageWidth = holder.ivCover.measuredWidth
              mImageHeight = holder.ivCover.measuredHeight

              loadImage(holder.ivCover, this@run.cover)

              return false
            }
          })
        } else {
          mImageWidth = holder.ivCover.measuredWidth
          mImageHeight = holder.ivCover.measuredHeight

          loadImage(holder.ivCover, cover)
        }
      } else {
        loadImage(holder.ivCover, cover)
      }

      holder.itemContainer.setOnClickListener { onMainItemClick?.invoke(position) }
    }
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun findItem(item: Audio): Int = items.indices.firstOrNull { items[it]?.url.equals(item.url) } ?: -1

  private fun loadImage(ivCover: SimpleDraweeView, coverUrl: String) {
    val imageUri = Uri.parse(coverUrl.trim())

    val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(imageUri)
            .setResizeOptions(ResizeOptions(mImageWidth, mImageHeight))
            .build()

    ivCover.controller = Fresco.newDraweeControllerBuilder()
            .setOldController(ivCover.controller)
            .setImageRequest(imageRequest)
            .build()
  }

  class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAudioName = view.findViewById<TextView>(R.id.tv_title)
    val ivCover = view.findViewById<SimpleDraweeView>(R.id.iv_cover)
    val ivGender = view.findViewById<ImageView>(R.id.iv_gender)
    val tvGender = view.findViewById<TextView>(R.id.tv_gender)
    val itemContainer = view.findViewById<View>(R.id.item_container)
    val stateArea = view.findViewById<View>(R.id.state_area)
    val ivLocked = view.findViewById<View>(R.id.iv_locked)
  }
}