package com.baha.sushigarden.features.productdetail

import androidx.lifecycle.ViewModel
import com.baha.sushigarden.data.models.Product
import com.baha.sushigarden.data.services.cart.CartService
import com.baha.sushigarden.data.services.catalog.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val cartService: CartService
) : ViewModel() {

    private val _product = MutableStateFlow<Product?>(null)
    val product = _product.asStateFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity = _quantity.asStateFlow()

    private val _selectedAddOns = MutableStateFlow<Set<String>>(emptySet())
    val selectedAddOns = _selectedAddOns.asStateFlow()

    fun loadProduct(id: String) { _product.value = menuRepository.getProduct(id) }

    fun increment() { _quantity.value++ }

    fun decrement() { if (_quantity.value > 1) _quantity.value-- }

    fun toggleAddOn(addOnId: String) {
        _selectedAddOns.value = _selectedAddOns.value.toMutableSet().also {
            if (it.contains(addOnId)) it.remove(addOnId) else it.add(addOnId)
        }
    }

    fun addToCart() {
        val p = _product.value ?: return
        val addOns = p.availableAddOns.filter { _selectedAddOns.value.contains(it.id) }
        cartService.addItem(p, addOns, _quantity.value)
    }
}
