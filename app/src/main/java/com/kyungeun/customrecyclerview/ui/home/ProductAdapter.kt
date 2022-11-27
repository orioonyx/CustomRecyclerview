package com.kyungeun.customrecyclerview.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.*
import com.kyungeun.customrecyclerview.data.entity.ProductList
import com.kyungeun.customrecyclerview.databinding.ItemProductLargeTypeBinding
import com.kyungeun.customrecyclerview.databinding.ItemProductMediumTypeBinding
import com.kyungeun.customrecyclerview.databinding.ItemProductSmallTypeBinding

/**
 * Includes 3 item types
 * Used ListAdapter to compare items
 */
class ProductAdapter() : ListAdapter<ProductList, RecyclerView.ViewHolder>(diffUtil) {
    private lateinit var context: Context

    //컨텐츠 뷰타입
    private val VIEW_TYPE_SMALL = 1
    private val VIEW_TYPE_MEDIUM = 2
    private val VIEW_TYPE_LARGE = 3

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            1 -> {
                VIEW_TYPE_SMALL
            }
            2 -> {
                VIEW_TYPE_MEDIUM
            }
            else -> {
                VIEW_TYPE_LARGE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        when (viewType) {
            1 -> {
                val binding: ItemProductSmallTypeBinding = ItemProductSmallTypeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return SmallProductViewHolder(binding)
            }
            2 -> {
                val binding: ItemProductMediumTypeBinding = ItemProductMediumTypeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return MediumProductViewHolder(binding)
            }
            else -> {
                val binding: ItemProductLargeTypeBinding = ItemProductLargeTypeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return LargeProductViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val current = getItem(position)
        when (holder.itemViewType) {
            VIEW_TYPE_SMALL -> {
                (holder as SmallProductViewHolder).bind(current)
            }
            VIEW_TYPE_MEDIUM -> {
                (holder as MediumProductViewHolder).bind(current)
            }
            VIEW_TYPE_LARGE -> {
                (holder as LargeProductViewHolder).bind(current)
            }
        }
    }

    inner class SmallProductViewHolder(private val itemBinding: ItemProductSmallTypeBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var products: ProductList

        @SuppressLint("SetTextI18n")
        fun bind(items: ProductList) {
            this.products = items
            itemBinding.title.text = products.title

            val recyclerview = itemBinding.recyclerviewProductSmall
            recyclerview.layoutManager =
                GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
            val adapter = ProductSmallAdapter()
            recyclerview.adapter = adapter
            adapter.setItems(products.productArray)

            adapter.setOnItemClickListener(object : ProductSmallAdapter.OnItemClickListener {
                override fun onProductSmallItemClicked(id: Int) {
                    Toast.makeText(context, "Small Item Click!", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    inner class MediumProductViewHolder(private val itemBinding: ItemProductMediumTypeBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var products: ProductList

        @SuppressLint("SetTextI18n")
        fun bind(items: ProductList) {
            this.products = items
            itemBinding.title.text = products.title

            val recyclerview = itemBinding.recyclerviewProductMedium
            recyclerview.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = ProductMediumAdapter()
            recyclerview.adapter = adapter
            adapter.setItems(products.productArray)

            adapter.setOnItemClickListener(object : ProductMediumAdapter.OnItemClickListener {
                override fun onProductMediumItemClicked(id: Int) {
                    Toast.makeText(context, "Medium Item Click!", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    inner class LargeProductViewHolder(private val itemBinding: ItemProductLargeTypeBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var products: ProductList

        @SuppressLint("SetTextI18n")
        fun bind(items: ProductList) {
            this.products = items
            itemBinding.title.text = products.title

            val recyclerview = itemBinding.recyclerviewProductLarge
            recyclerview.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = ProductLargeAdapter()
            recyclerview.adapter = adapter
            adapter.setItems(products.productArray)

            adapter.setOnItemClickListener(object : ProductLargeAdapter.OnItemClickListener {
                override fun onProductLargeItemClicked(id: Int) {
                    Toast.makeText(context, "Large Item Click!", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ProductList>() {
            override fun areContentsTheSame(oldItem: ProductList, newItem: ProductList): Boolean {
                return oldItem == newItem
            }
            override fun areItemsTheSame(oldItem: ProductList, newItem: ProductList): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
