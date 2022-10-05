package com.kyungeun.customrecyclerview.data.repository

import androidx.lifecycle.MutableLiveData
import com.kyungeun.customrecyclerview.R
import com.kyungeun.customrecyclerview.data.entity.Banner
import com.kyungeun.customrecyclerview.data.entity.Product
import com.kyungeun.customrecyclerview.data.entity.ProductList
import javax.inject.Inject

class HomeRepository @Inject constructor() {

    fun getMainBannerList(): MutableLiveData<List<Banner>> {

        val data = ArrayList<Banner>()
        data.add(Banner(0, R.drawable.main1))
        data.add(Banner(1, R.drawable.main2))
        data.add(Banner(2, R.drawable.main3))
        data.add(Banner(3, R.drawable.main4))
        data.add(Banner(4, R.drawable.main5))
        data.add(Banner(5, R.drawable.main6))
        return MutableLiveData(data)
    }

    fun getEventBannerList(): MutableLiveData<List<Banner>> {

        val data = ArrayList<Banner>()
        data.add(Banner(0, R.drawable.event1))
        data.add(Banner(1, R.drawable.event2))
        data.add(Banner(2, R.drawable.event3))
        data.add(Banner(2, R.drawable.event4))
        return MutableLiveData(data)
    }

    fun getProductList(): MutableLiveData<List<ProductList>> {
        val data = ArrayList<ProductList>()

        val product = ArrayList<Product>()
        product.add(Product(0, R.drawable.demo1, "이름", 10000))
        product.add(Product(1, R.drawable.demo2, "이름", 10000))
        product.add(Product(2, R.drawable.demo3, "이름", 10000))
        product.add(Product(3, R.drawable.demo4, "이름", 10000))
        product.add(Product(4, R.drawable.demo5, "이름", 10000))
        product.add(Product(5, R.drawable.demo6, "이름", 10000))
        data.add(ProductList(0, 1, "제목", product))

        val product2 = ArrayList<Product>()
        product2.add(Product(0, R.drawable.demo2, "이름", 10000))
        product2.add(Product(0, R.drawable.demo2, "이름", 10000))
        data.add(ProductList(0, 2, "제목", product2))

        val product3 = ArrayList<Product>()
        product3.add(Product(0, R.drawable.demo3, "이름", 10000))
        data.add(ProductList(0, 3, "제목", product3))

        return MutableLiveData(data)
    }
}