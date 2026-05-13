package com.example.oceanfresh.data.model

data class Product(
    val id: Int,
    val name: String,
    val category: String,
    val price: Double,          // price per unit
    val unit: String,           // e.g. "500g", "1 kg", "dozen"
    val imageUrl: String,       // emoji or URL — we use emoji placeholders
    val discount: Int = 0,      // discount % (0 = no discount)
    val isOrganic: Boolean = false
) {
    /** Price after discount */
    val discountedPrice: Double
        get() = if (discount > 0) price * (1 - discount / 100.0) else price

    val originalPrice: Double get() = price
}