package com.kyungeun.customrecyclerview.ui.banner

import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyungeun.customrecyclerview.data.entity.Banner
import com.kyungeun.customrecyclerview.databinding.ActivityBannerBinding
import com.kyungeun.customrecyclerview.util.CenterZoomLayoutManager
import com.kyungeun.customrecyclerview.util.GravitySnapHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BannerActivity : AppCompatActivity(), BannerAdapter.BannerItemListener {

    private lateinit var binding: ActivityBannerBinding
    private val viewModel: BannerViewModel by viewModels()
    private lateinit var adapter: BannerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = BannerAdapter(this)
        binding.recyclerviewBanner.layoutManager = CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerviewBanner.isNestedScrollingEnabled = false
        binding.recyclerviewBanner.adapter = adapter

        //center snap
        val snapHelper = GravitySnapHelper(Gravity.CENTER)
        snapHelper.scrollMsPerInch = 25f //scroll speed
        snapHelper.attachToRecyclerView(binding.recyclerviewBanner)

    }

    private fun setupObservers() {
        viewModel.bannerList.observe(this) {
            adapter.setItems(it as ArrayList<Banner>)
        }
    }

    override fun onClicked(id: Int) {

    }


}