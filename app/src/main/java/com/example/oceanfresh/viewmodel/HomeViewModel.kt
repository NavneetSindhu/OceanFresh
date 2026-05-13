package com.example.oceanfresh.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oceanfresh.data.model.Product
import com.example.oceanfresh.data.repository.GroceryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: GroceryRepository) : ViewModel() {

    // Now fetched from the repository instance
    val categories = repository.categories

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Initially we load all products from the repository's logic
    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    init {
        // Initial load of products
        applyFilter()
    }

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
        applyFilter()
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    private fun applyFilter() {
        // Since getProducts is likely a standard function in your repo,
        // we call it and update the StateFlow.
        _filteredProducts.value = repository.getProducts(
            category = _selectedCategory.value,
            query    = _searchQuery.value
        )
    }
}