package com.example.oceanfresh.data.local

import androidx.room.TypeConverter
import com.example.oceanfresh.data.model.CartItem
import com.example.oceanfresh.data.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromProduct(product: Product): String = gson.toJson(product)

    @TypeConverter
    fun toProduct(json: String): Product = gson.fromJson(json, Product::class.java)

    @TypeConverter
    fun fromCartItemList(list: List<CartItem>): String = gson.toJson(list)

    @TypeConverter
    fun toCartItemList(json: String): List<CartItem> {
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(json, type)
    }
}