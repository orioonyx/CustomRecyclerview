package com.kyungeun.customrecyclerview.data.entity

data class Product (
    var id: Int,
    var type: String,
    var image: Int,
    var name: String,
    var price: Int,
    var isSoldOut: Boolean
)