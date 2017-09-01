package com.essential.audio.ui.home

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.essential.audio.R
import com.essential.audio.data.model.Audio
import selft.yue.basekotlin.adapter.normal.BaseAdapter

/**
 * Created by dongc on 9/1/2017.
 */
class AudiosAdapter : BaseAdapter<Audio, AudiosAdapter.ItemHolder>() {
    var onMainItemClick: ((audio: Audio?) -> Unit)? = null
        get() = field
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder =
            ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_audio, parent, false))

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val audio = items[position]
        holder.tvAudioName.text = audio?.name

        holder.tvAudioName.setOnClickListener { onMainItemClick?.invoke(audio) }
    }

    override fun findItem(item: Audio): Int = items.indices.firstOrNull { items[it]?.url.equals(item.url) } ?: -1

    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAudioName: TextView = view.findViewById(R.id.tv_audio_name)
    }
}