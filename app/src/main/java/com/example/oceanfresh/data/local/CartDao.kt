package com.example.oceanfresh.data.local

import androidx.room.*
import com.example.oceanfresh.data.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("SELECT * FROM cart_table")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: CartItem)

    @Delete
    suspend fun deleteItem(item: CartItem)

    @Query("DELETE FROM cart_table")
    suspend fun clearCart()
}