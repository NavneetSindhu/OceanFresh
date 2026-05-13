package com.example.oceanfresh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oceanfresh.data.model.CartItem
import com.example.oceanfresh.data.model.Order
import com.example.oceanfresh.data.model.Product
import com.example.oceanfresh.data.repository.GroceryRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class CartViewModel(private val repository: GroceryRepository) : ViewModel() {

    // ── 1. PERSISTENT STATE ──────────────────────────────────────────────────

    /**
     * Observe Room data directly from the Repository.
     * Converting it to StateFlow so the UI can collect it lifecycle-aware.
     */
    val cartItems: StateFlow<List<CartItem>> = repository.allCartItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Derived Map for O(1) lookup in the Product Grid.
     * We derive this from the cartItems list to ensure consistency.
     */
    val cartMap: StateFlow<Map<Int, CartItem>> = cartItems
        .map { list -> list.associateBy { it.productId } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // ── 2. CALCULATED PROPERTIES ─────────────────────────────────────────────

    val cartCount: StateFlow<Int> = cartItems
        .map { list -> list.sumOf { it.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val cartTotal: StateFlow<Double> = cartItems
        .map { list -> list.sumOf { it.totalPrice } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _lastOrder = MutableStateFlow<Order?>(null)
    val lastOrder: StateFlow<Order?> = _lastOrder.asStateFlow()

    // Fees constants
    val deliveryFee = 25.0
    val platformFee = 5.0
    val grandTotal: Double get() = (cartTotal.value + deliveryFee + platformFee)

    // ── 3. CART ACTIONS (ROOM PERSISTENCE) ───────────────────────────────────

    fun addToCart(product: Product) {
        viewModelScope.launch {
            val existing = cartMap.value[product.id]
            val updatedItem = if (existing != null) {
                existing.copy(quantity = existing.quantity + 1)
            } else {
                CartItem(productId = product.id, product = product, quantity = 1)
            }
            repository.updateCartItem(updatedItem)
        }
    }

    fun removeOneFromCart(product: Product) {
        viewModelScope.launch {
            val existing = cartMap.value[product.id] ?: return@launch
            if (existing.quantity <= 1) {
                repository.deleteCartItem(existing)
            } else {
                repository.updateCartItem(existing.copy(quantity = existing.quantity - 1))
            }
        }
    }

    fun removeFromCart(productId: Int) {
        viewModelScope.launch {
            val item = cartMap.value[productId] ?: return@launch
            repository.deleteCartItem(item)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearLocalCart()
        }
    }

    // ── 4. CHECKOUT LOGIC ───────────────────────────────────────────────────

    fun placeOrder(address: String, paymentMethod: String): Order {
        val order = Order(
            orderId = "FX" + UUID.randomUUID().toString().takeLast(6).uppercase(),
            items = cartItems.value,
            deliveryAddress = address,
            paymentMethod = paymentMethod,
            totalAmount = grandTotal,
            estimatedDeliveryMinutes = (8..15).random()
        )
        _lastOrder.value = order
        clearCart() // This now clears the Room Database automatically
        return order
    }
}