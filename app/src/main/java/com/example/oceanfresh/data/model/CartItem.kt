package com.example.oceanfresh.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_table")
data class CartItem(
    @PrimaryKey val productId: Int, // Use product.id as PK
    val product: Product,
    val quantity: Int
) {
    val totalPrice: Double get() = product.discountedPrice * quantity
}