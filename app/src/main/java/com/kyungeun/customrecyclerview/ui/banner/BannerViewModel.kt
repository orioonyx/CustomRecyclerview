package com.kyungeun.customrecyclerview.ui.banner

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kyungeun.customrecyclerview.data.entity.Banner
import com.kyungeun.customrecyclerview.data.repository.BannerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BannerViewModel @Inject constructor(
    private val bannerRepository: BannerRepository
) : ViewModel() {

    val bannerList : MutableLiveData<List<Banner>> = bannerRepository.getBannerList()
}
