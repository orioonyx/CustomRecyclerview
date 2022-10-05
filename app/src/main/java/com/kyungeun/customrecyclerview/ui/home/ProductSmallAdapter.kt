package com.kyungeun.customrecyclerview.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kyungeun.customrecyclerview.data.entity.Product
import com.kyungeun.customrecyclerview.databinding.ItemProductDetailBinding

class ProductSmallAdapter() : RecyclerView.Adapter<ProductSmallViewHolder>() {

    lateinit var mListener : OnItemClickListener

    interface OnItemClickListener {
        fun onProductSmallItemClicked(id: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    private val items = ArrayList<Product>()

    fun setItems(items: ArrayList<Product>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSmallViewHolder {
        val binding: ItemProductDetailBinding = ItemProductDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductSmallViewHolder(binding, mListener)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ProductSmallViewHolder, position: Int) = holder.bind(items[position])
}

class ProductSmallViewHolder(private val itemBinding: ItemProductDetailBinding, private val listener: ProductSmallAdapter.OnItemClickListener) : RecyclerView.ViewHolder(itemBinding.root),
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
            .into(itemBinding.image)

        itemBinding.name.text = product.name
        itemBinding.price.text = product.price.toString()
    }

    override fun onClick(v: View?) {
        listener.onProductSmallItemClicked(product.id)
    }
}

