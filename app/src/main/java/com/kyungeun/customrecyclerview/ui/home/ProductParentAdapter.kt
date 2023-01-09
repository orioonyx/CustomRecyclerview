package com.kyungeun.customrecyclerview.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kyungeun.customrecyclerview.data.entity.Product
import com.kyungeun.customrecyclerview.data.entity.ProductList
import com.kyungeun.customrecyclerview.databinding.ItemProductParentBinding

/**
 * Nested RecyclerView Adapter
 * - Includes 3 item types
 * - Used ListAdapter to compare items
 */
class ProductParentAdapter() : ListAdapter<ProductList, RecyclerView.ViewHolder>(diffUtil) {
    private lateinit var context: Context

    /**
     * Optimize the scrolling performance of the Nested RecyclerView using the setRecycledViewPool.
     * - The RecyclerView will use the same pool for all the nested RecyclerViews.
     */
    private val viewPool = RecyclerView.RecycledViewPool()

    // viewType
    private val viewTypeSmall = 1
    private val viewTypeMedium = 2
    private val viewTypeLarge = 3

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            1 -> {
                viewTypeSmall
            }
            2 -> {
                viewTypeMedium
            }
            else -> {
                viewTypeLarge
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context

        val binding: ItemProductParentBinding = ItemProductParentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return when (viewType) {
            1 -> {
                SmallProductViewHolder(binding)
            }
            2 -> {
                MediumProductViewHolder(binding)
            }
            else -> {
                LargeProductViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val current = getItem(position)
        when (holder.itemViewType) {
            viewTypeSmall -> {
                (holder as SmallProductViewHolder).bind(current)
            }
            viewTypeMedium -> {
                (holder as MediumProductViewHolder).bind(current)
            }
            viewTypeLarge -> {
                (holder as LargeProductViewHolder).bind(current)
            }
        }
    }

    inner class SmallProductViewHolder(private val itemBinding: ItemProductParentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var products: ProductList

        fun bind(items: ProductList) {
            this.products = items
            itemBinding.title.text = products.title

            val recyclerview = itemBinding.recyclerviewProductChild

            recyclerview.setRecycledViewPool(viewPool)

            recyclerview.layoutManager =
                GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
            val adapter = ProductChildAdapter()
            recyclerview.adapter = adapter
            adapter.setItemWidth(dpToPx(150f))
            adapter.setItems(products.productArray)

            adapter.setOnItemClickListener(object : ProductChildAdapter.OnItemClickListener {
                override fun onProductChildItemClicked(product: Product) {
                    Toast.makeText(context, "Clicked ${product.name} $${product.price}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    inner class MediumProductViewHolder(private val itemBinding: ItemProductParentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var products: ProductList

        fun bind(items: ProductList) {
            this.products = items
            itemBinding.title.text = products.title

            val recyclerview = itemBinding.recyclerviewProductChild

            recyclerview.setRecycledViewPool(viewPool)

            recyclerview.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = ProductChildAdapter()
            recyclerview.adapter = adapter
            adapter.setItemWidth(dpToPx(180f))
            adapter.setItems(products.productArray)

            adapter.setOnItemClickListener(object : ProductChildAdapter.OnItemClickListener {
                override fun onProductChildItemClicked(product: Product) {
                    Toast.makeText(context, "Clicked ${product.name} $${product.price}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    inner class LargeProductViewHolder(private val itemBinding: ItemProductParentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        private lateinit var products: ProductList

        fun bind(items: ProductList) {
            this.products = items
            itemBinding.title.text = products.title

            val recyclerview = itemBinding.recyclerviewProductChild

            recyclerview.setRecycledViewPool(viewPool)

            recyclerview.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = ProductChildAdapter()
            recyclerview.adapter = adapter
            adapter.setItems(products.productArray)

            adapter.setOnItemClickListener(object : ProductChildAdapter.OnItemClickListener {
                override fun onProductChildItemClicked(product: Product) {
                    Toast.makeText(context, "Clicked ${product.name} $${product.price}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun dpToPx(dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
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
