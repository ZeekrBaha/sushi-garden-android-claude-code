package com.baha.sushigarden.data.services.cart

import com.baha.sushigarden.data.models.AddOn
import com.baha.sushigarden.data.models.CartState
import com.baha.sushigarden.data.models.Product
import kotlinx.coroutines.flow.StateFlow

interface CartService {
    val cartState: StateFlow<CartState>
    fun addItem(product: Product, addOns: List<AddOn> = emptyList())
    fun removeItem(productId: String)
    fun clearCart()
}
