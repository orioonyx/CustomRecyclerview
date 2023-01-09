package com.kyungeun.customrecyclerview.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kyungeun.customrecyclerview.data.entity.Product
import com.kyungeun.customrecyclerview.databinding.ItemProductChildBinding

class ProductChildAdapter() : RecyclerView.Adapter<ProductChildViewHolder>() {

    lateinit var mListener : OnItemClickListener

    interface OnItemClickListener {
        fun onProductChildItemClicked(product: Product)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    private val items = ArrayList<Product>()

    private var itemWidth = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: ArrayList<Product>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun setItemWidth(width: Int) {
        this.itemWidth = width
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductChildViewHolder {
        val binding: ItemProductChildBinding = ItemProductChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductChildViewHolder(binding, mListener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProductChildViewHolder, position: Int){
        holder.bind(items[position])

        // Set item width
        if(itemWidth != 0) {
            holder.itemView.layoutParams.width = itemWidth
        } else {
            holder.itemView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }
}

class ProductChildViewHolder(private val itemBinding: ItemProductChildBinding, private val listener: ProductChildAdapter.OnItemClickListener) : RecyclerView.ViewHolder(itemBinding.root),
    View.OnClickListener {

    private lateinit var product: Product

    init {
        itemBinding.root.setOnClickListener(this)
    }

    @SuppressLint("SetTextI18n")
    fun bind(item: Product) {
        this.product = item

        Glide.with(itemBinding.root)
            .load(item.image)
            .override(512, 512)
            .dontAnimate()
            .skipMemoryCache(true)
            .into(itemBinding.image)

        itemBinding.name.text = product.name
        itemBinding.price.text = "$${product.price}"
    }

    override fun onClick(v: View?) {
        listener.onProductChildItemClicked(product)
    }
}

