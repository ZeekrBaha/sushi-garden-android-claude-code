package com.baha.sushigarden.data.services.cart

import com.baha.sushigarden.data.models.AddOn
import com.baha.sushigarden.data.models.CartItem
import com.baha.sushigarden.data.models.CartState
import com.baha.sushigarden.data.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemoryCartService : CartService {
    private val _state = MutableStateFlow(CartState())
    override val cartState: StateFlow<CartState> = _state.asStateFlow()

    override fun addItem(product: Product, addOns: List<AddOn>) {
        val items = _state.value.items.toMutableList()
        val idx = items.indexOfFirst { it.product.id == product.id }
        if (idx >= 0) {
            items[idx] = items[idx].copy(quantity = items[idx].quantity + 1)
        } else {
            items.add(CartItem(product, 1, addOns))
        }
        _state.value = CartState(items)
    }

    override fun removeItem(productId: String) {
        val items = _state.value.items.toMutableList()
        val idx = items.indexOfFirst { it.product.id == productId }
        if (idx >= 0) {
            val item = items[idx]
            if (item.quantity > 1) items[idx] = item.copy(quantity = item.quantity - 1)
            else items.removeAt(idx)
        }
        _state.value = CartState(items)
    }

    override fun clearCart() {
        _state.value = CartState()
    }
}
