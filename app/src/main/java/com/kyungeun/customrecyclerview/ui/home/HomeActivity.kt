package com.kyungeun.customrecyclerview.ui.home

import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyungeun.customrecyclerview.data.entity.Banner
import com.kyungeun.customrecyclerview.data.entity.ProductList
import com.kyungeun.customrecyclerview.databinding.ActivityHomeBinding
import com.kyungeun.customrecyclerview.util.GravitySnapHelper
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), MainBannerAdapter.BannerItemListener,
    EventBannerAdapter.BannerItemListener {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var mainBannerAdapter: MainBannerAdapter
    private lateinit var eventBannerAdapter: EventBannerAdapter
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        mainBannerAdapter = MainBannerAdapter(this)
        binding.recyclerviewMainBanner.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewMainBanner.isNestedScrollingEnabled = false
        binding.recyclerviewMainBanner.adapter = mainBannerAdapter

        val mainbannerSnapHelper = GravitySnapHelper(Gravity.CENTER)
        mainbannerSnapHelper.setScrollMsPerInch(25f)
        mainbannerSnapHelper.attachToRecyclerView(binding.recyclerviewMainBanner)

        eventBannerAdapter = EventBannerAdapter(this)
        binding.recyclerviewEventBanner.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewEventBanner.isNestedScrollingEnabled = false
        binding.recyclerviewEventBanner.adapter = eventBannerAdapter

        val eventbannerSnapHelper = GravitySnapHelper(Gravity.CENTER)
        eventbannerSnapHelper.setScrollMsPerInch(25f)
        eventbannerSnapHelper.attachToRecyclerView(binding.recyclerviewEventBanner)

        productAdapter = ProductAdapter()
        binding.recyclerviewProduct.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewProduct.isNestedScrollingEnabled = false
        binding.recyclerviewProduct.adapter = productAdapter
    }

    private fun setupObservers() {
        viewModel.mainbannerList.observe(this) {
            mainBannerAdapter.setItems(it as ArrayList<Banner>)
        }
        viewModel.eventbannerList.observe(this) {
            eventBannerAdapter.setItems(it as ArrayList<Banner>)
        }
        viewModel.productList.observe(this) {
            productAdapter.setItems(it as ArrayList<ProductList>)
        }
    }

    override fun onMainBannerClicked(id: Int) {
    }

    override fun onEventBannerClicked(id: Int) {
    }
}