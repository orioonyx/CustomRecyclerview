package com.kyungeun.customrecyclerview.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kyungeun.customrecyclerview.data.entity.Banner
import com.kyungeun.customrecyclerview.databinding.ItemEventbannerBinding

class EventBannerAdapter(private val listener: BannerItemListener) : RecyclerView.Adapter<EventBannerViewHolder>() {

    interface BannerItemListener {
        fun onEventBannerClicked(id: Int)
    }

    private val items = ArrayList<Banner>()

    fun setItems(items: ArrayList<Banner>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventBannerViewHolder {
        val binding: ItemEventbannerBinding = ItemEventbannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventBannerViewHolder(binding, listener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: EventBannerViewHolder, position: Int) = holder.bind(items[position])
}

class EventBannerViewHolder(private val itemBinding: ItemEventbannerBinding, private val listener: EventBannerAdapter.BannerItemListener) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var banner: Banner

    init {
        itemBinding.root.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: Banner) {
        this.banner = item

        Glide.with(itemBinding.root)
            .load(item.image)
            .override(512, 512)
            .dontAnimate()
            .into(itemBinding.imageView)
    }

    override fun onClick(v: View?) {
        listener.onEventBannerClicked(banner.id)
    }
}

