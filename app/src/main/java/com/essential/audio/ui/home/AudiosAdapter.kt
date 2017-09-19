package com.essential.audio.ui.home

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
import com.essential.audio.R
import com.essential.audio.data.model.Audio
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequest
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

  var onMainItemClick: ((position: Int) -> Unit)? = null
    get() = field
    set(value) {
      field = value
    }

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

      holder.ivCover.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
          if (Build.VERSION.SDK_INT < 16)
            holder.ivCover.viewTreeObserver.removeGlobalOnLayoutListener(this)
          else
            holder.ivCover.viewTreeObserver.removeOnGlobalLayoutListener(this)

          val imageUri = Uri.parse(this@run.cover.trim())

          val imageRequest = ImageRequestBuilder
                  .newBuilderWithSource(imageUri)
                  .setResizeOptions(ResizeOptions(holder.ivCover.width, holder.ivCover.height))
                  .build()

          holder.ivCover.controller = Fresco.newDraweeControllerBuilder()
                  .setOldController(holder.ivCover.controller)
                  .setImageRequest(imageRequest)
                  .build()
        }
      })

      holder.itemContainer.setOnClickListener { onMainItemClick?.invoke(position) }
    }
  }

  override fun getItemCount(): Int {
    return items.size
  }

  override fun findItem(item: Audio): Int = items.indices.firstOrNull { items[it]?.url.equals(item.url) } ?: -1

  class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
    val tvAudioName = view.findViewById<TextView>(R.id.tv_title)
    val ivCover = view.findViewById<SimpleDraweeView>(R.id.iv_cover)
    val ivGender = view.findViewById<ImageView>(R.id.iv_gender)
    val tvGender = view.findViewById<TextView>(R.id.tv_gender)
    val itemContainer = view.findViewById<View>(R.id.item_container)
  }
}