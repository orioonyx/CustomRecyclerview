package com.kyungeun.customrecyclerview.data.repository

import androidx.lifecycle.MutableLiveData
import com.kyungeun.customrecyclerview.R
import com.kyungeun.customrecyclerview.data.entity.Banner
import javax.inject.Inject

class BannerRepository @Inject constructor() {

    fun getBannerList(): MutableLiveData<List<Banner>> {

        val data = ArrayList<Banner>()
        data.add(Banner(0, R.drawable.demo1))
        data.add(Banner(1, R.drawable.demo2))
        data.add(Banner(2, R.drawable.demo3))
        data.add(Banner(3, R.drawable.demo4))
        data.add(Banner(4, R.drawable.demo5))
        data.add(Banner(5, R.drawable.demo6))
        data.add(Banner(6, R.drawable.demo7))
        data.add(Banner(7, R.drawable.demo8))
        return MutableLiveData(data)
    }
}