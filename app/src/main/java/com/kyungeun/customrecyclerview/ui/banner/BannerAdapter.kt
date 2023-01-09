package com.kyungeun.customrecyclerview.ui.banner

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kyungeun.customrecyclerview.data.entity.Banner
import com.kyungeun.customrecyclerview.databinding.ItemBannerBinding

class BannerAdapter(private val listener: BannerItemListener) : RecyclerView.Adapter<BannerViewHolder>() {

    interface BannerItemListener {
        fun onClicked(id: Int)
    }

    private val items = ArrayList<Banner>()

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: ArrayList<Banner>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val binding: ItemBannerBinding = ItemBannerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BannerViewHolder(binding, listener)
    }

    // infinite scroll
    // original code : override fun getItemCount(): Int = items.size
    override fun getItemCount(): Int {
       return Integer.MAX_VALUE
    }

    // infinite scroll
    // original code : override fun onBindViewHolder(holder: BannerViewHolder, position: Int) = holder.bind(items[position])
    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
       val realPosition = position % items.size
        return holder.bind(items[realPosition])
    }
}

class BannerViewHolder(private val itemBinding: ItemBannerBinding, private val listener: BannerAdapter.BannerItemListener) : RecyclerView.ViewHolder(itemBinding.root),
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
        listener.onClicked(banner.id)
    }
}

