package com.baha.sushigarden.features.cart

import androidx.lifecycle.ViewModel
import com.baha.sushigarden.data.models.CartState
import com.baha.sushigarden.data.services.cart.CartService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val cartService: CartService) : ViewModel() {
    val cartState: StateFlow<CartState> = cartService.cartState

    fun increment(productId: String) {
        val item = cartState.value.items.firstOrNull { it.product.id == productId } ?: return
        cartService.addItem(item.product, item.selectedAddOns)
    }

    fun decrement(productId: String) = cartService.removeItem(productId)
}
