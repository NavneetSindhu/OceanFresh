package com.example.oceanfresh.data.model

data class Order(
    val orderId: String,
    val items: List<CartItem>,
    val deliveryAddress: String,
    val paymentMethod: String,  // "COD" or "Online"
    val totalAmount: Double,
    val estimatedDeliveryMinutes: Int = 10
)