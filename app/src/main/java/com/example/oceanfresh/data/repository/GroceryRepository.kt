package com.example.oceanfresh.data.repository

import com.example.oceanfresh.data.local.CartDao
import com.example.oceanfresh.data.model.CartItem
import com.example.oceanfresh.data.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * REPOSITORY LAYER
 * Now uses CartDao for local Room persistence.
 */
class GroceryRepository(private val cartDao: CartDao) {

    // ── Static Metadata ───────────────────────────────────────────────────
    val categories = listOf(
        "All", "Fruits", "Vegetables", "Dairy", "Snacks", "Beverages", "Bakery"
    )

    // Hardcoded for now; will be replaced by Retrofit later
    private val staticProducts = listOf(
        Product(1,  "Fresh Apples",      "Fruits",     89.0,  "1 kg",   "🍎", discount = 10, isOrganic = true),
        Product(2,  "Bananas",           "Fruits",     45.0,  "dozen",  "🍌"),
        Product(7,  "Baby Spinach",      "Vegetables", 35.0,  "200g",   "🥬", isOrganic = true),
        Product(13, "Full Cream Milk",   "Dairy",      62.0,  "1 L",    "🥛"),
        Product(17, "Lays Classic",      "Snacks",     20.0,  "73g",    "🍟"),
        Product(21, "Orange Juice",      "Beverages",  89.0,  "1 L",    "🧃", isOrganic = true),
        Product(24, "Whole Wheat Bread", "Bakery",     55.0,  "400g",   "🍞")
    )

    // ── Product Logic ─────────────────────────────────────────────────────
    fun getProducts(category: String = "All", query: String = ""): List<Product> {
        var result = if (category == "All") staticProducts else staticProducts.filter { it.category == category }
        if (query.isNotBlank()) {
            result = result.filter { it.name.contains(query, ignoreCase = true) }
        }
        return result
    }

    // ── Room Cart Logic (The Upgrade) ─────────────────────────────────────

    /** Returns a Flow of cart items that UI can observe reactively */
    val allCartItems: Flow<List<CartItem>> = cartDao.getAllCartItems()

    suspend fun updateCartItem(item: CartItem) {
        cartDao.insertOrUpdate(item)
    }

    suspend fun deleteCartItem(item: CartItem) {
        cartDao.deleteItem(item)
    }

    suspend fun clearLocalCart() {
        cartDao.clearCart()
    }
}