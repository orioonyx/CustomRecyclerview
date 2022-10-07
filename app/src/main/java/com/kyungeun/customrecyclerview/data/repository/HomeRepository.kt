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
        data.add(Banner(0, R.drawable.main))
        data.add(Banner(1, R.drawable.main1))
        data.add(Banner(2, R.drawable.main2))
        data.add(Banner(3, R.drawable.main3))
        data.add(Banner(4, R.drawable.main4))
        data.add(Banner(5, R.drawable.main5))
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
        product.add(Product(0, R.drawable.product1, "demo", 56.00))
        product.add(Product(0, R.drawable.product2, "demo", 28.00))
        product.add(Product(0, R.drawable.product3, "demo", 84.00))
        product.add(Product(0, R.drawable.product4, "demo", 91.00))
        product.add(Product(0, R.drawable.product5, "demo", 19.00))
        product.add(Product(0, R.drawable.product6, "demo", 24.00))
        product.add(Product(0, R.drawable.product1, "demo", 56.00))
        product.add(Product(0, R.drawable.product2, "demo", 28.00))
        product.add(Product(0, R.drawable.product3, "demo", 84.00))
        product.add(Product(0, R.drawable.product4, "demo", 91.00))
        data.add(ProductList(0, 1, "New Items", product))

        val product2 = ArrayList<Product>()
        product2.add(Product(0, R.drawable.product7, "demo", 19.00))
        product2.add(Product(0, R.drawable.product8, "demo", 25.00))
        product2.add(Product(0, R.drawable.product9, "demo", 36.00))
        product2.add(Product(0, R.drawable.product10, "demo", 12.00))
        product2.add(Product(0, R.drawable.product11, "demo", 25.00))
        data.add(ProductList(0, 2, "Popular Items", product2))

        val product3 = ArrayList<Product>()
        product3.add(Product(0, R.drawable.product10, "demo", 12.00))
        product3.add(Product(0, R.drawable.product11, "demo", 25.00))
        product3.add(Product(0, R.drawable.product12, "demo", 36.00))
        product3.add(Product(0, R.drawable.product13, "demo", 65.00))
        product3.add(Product(0, R.drawable.product7, "demo", 19.00))
        product3.add(Product(0, R.drawable.product8, "demo", 25.00))
        data.add(ProductList(0, 2, "Sale Items", product3))

        val product4 = ArrayList<Product>()
        product4.add(Product(0, R.drawable.jordan, "demo", 99.00))
        data.add(ProductList(0, 3, "Nike Jordan", product4))

        return MutableLiveData(data)
    }
}