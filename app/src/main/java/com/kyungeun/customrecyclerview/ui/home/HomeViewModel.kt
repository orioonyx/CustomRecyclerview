package com.kyungeun.customrecyclerview.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kyungeun.customrecyclerview.data.entity.Banner
import com.kyungeun.customrecyclerview.data.entity.ProductList
import com.kyungeun.customrecyclerview.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    val mainbannerList : MutableLiveData<List<Banner>> = homeRepository.getMainBannerList()
    val eventbannerList : MutableLiveData<List<Banner>> = homeRepository.getEventBannerList()
    val productList : MutableLiveData<List<ProductList>> = homeRepository.getProductList()
}
