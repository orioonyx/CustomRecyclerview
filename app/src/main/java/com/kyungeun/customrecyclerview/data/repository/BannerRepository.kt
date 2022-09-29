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

        return MutableLiveData(data)
    }
}