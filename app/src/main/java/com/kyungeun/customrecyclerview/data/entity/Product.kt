package com.kyungeun.customrecyclerview.data.entity

data class ProductList (
    var id: Int,
    var type: Int,
    var title: String,
    var productArray: ArrayList<Product>
)

data class Product (
    var id: Int,
    var image: Int,
    var name: String,
    var price: Int
)