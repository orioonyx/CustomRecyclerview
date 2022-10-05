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
        return MutableLiveData(data)
    }

    fun getEventBannerList(): MutableLiveData<List<Banner>> {

        val data = ArrayList<Banner>()
        data.add(Banner(0, R.drawable.event1))
        data.add(Banner(1, R.drawable.event2))
        data.add(Banner(2, R.drawable.event3))
        return MutableLiveData(data)
    }

    fun getProductList(): MutableLiveData<List<ProductList>> {
        val data = ArrayList<ProductList>()

        val product = ArrayList<Product>()
        product.add(Product(0, R.drawable.product1, "abcde", 16.00))
        product.add(Product(1, R.drawable.product2, "abcde", 20.00))
        product.add(Product(2, R.drawable.product3, "abcde", 12.00))
        product.add(Product(3, R.drawable.product4, "abcde", 9.00))
        product.add(Product(4, R.drawable.product5, "abcde", 19.00))
        product.add(Product(5, R.drawable.product6, "abcde", 24.00))
        data.add(ProductList(0, 1, "New Items", product))

        val product2 = ArrayList<Product>()
        product2.add(Product(0, R.drawable.product7, "abcde", 10.00))
        product2.add(Product(0, R.drawable.product8, "abcde", 25.00))
        product2.add(Product(0, R.drawable.product9, "abcde", 36.00))
        data.add(ProductList(0, 2, "Popular Items", product2))

        val product3 = ArrayList<Product>()
        product3.add(Product(0, R.drawable.product10, "abcde", 10.00))
        product3.add(Product(0, R.drawable.product11, "abcde", 25.00))
        product3.add(Product(0, R.drawable.product12, "abcde", 36.00))
        product3.add(Product(0, R.drawable.product13, "abcde", 15.00))
        data.add(ProductList(0, 2, "Sale Items", product3))

        val product4 = ArrayList<Product>()
        product4.add(Product(0, R.drawable.jordan, "abcde", 10.00))
        data.add(ProductList(0, 3, "Nike Jordan", product4))

        return MutableLiveData(data)
    }
}