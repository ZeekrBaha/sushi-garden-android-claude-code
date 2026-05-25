package com.baha.sushigarden.features.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.models.DeliveryAddress
import com.baha.sushigarden.data.models.Order
import com.baha.sushigarden.data.models.OrderLine
import com.baha.sushigarden.data.services.cart.CartService
import com.baha.sushigarden.data.services.orders.OrderDao
import com.baha.sushigarden.data.services.orders.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val cartService: CartService,
    private val orderDao: OrderDao
) : ViewModel() {

    val cartState = cartService.cartState
    val street    = MutableStateFlow("")
    val recipient = MutableStateFlow("")
    val phone     = MutableStateFlow("")
    val email     = MutableStateFlow("")

    val canConfirm: StateFlow<Boolean> = combine(
        cartState, street, recipient, phone, email
    ) { cart, s, r, p, e ->
        cart.items.isNotEmpty() &&
        s.isNotBlank() &&
        r.isNotBlank() &&
        p.isNotBlank() &&
        e.contains("@") && e.contains(".")
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _orderState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val orderState: StateFlow<UiState<String>> = _orderState.asStateFlow()

    fun confirm() = viewModelScope.launch {
        val cart = cartState.value
        if (cart.items.isEmpty()) {
            _orderState.value = UiState.Error("Корзина пуста")
            return@launch
        }
        if (street.value.isBlank() || recipient.value.isBlank() || phone.value.isBlank() ||
            !email.value.contains("@") || !email.value.contains(".")
        ) {
            _orderState.value = UiState.Error("Заполните все поля корректно")
            return@launch
        }
        _orderState.value = UiState.Loading
        try {
            val address = DeliveryAddress(street.value, recipient.value, phone.value, email.value)
            val lines = cart.items.map { OrderLine(it.product.name, it.quantity, it.lineTotal) }
            val order = Order(
                id = UUID.randomUUID().toString(),
                lines = lines,
                subtotal = cart.subtotal,
                deliveryFee = cart.deliveryFee,
                serviceFee = cart.serviceFee,
                total = cart.total,
                address = address
            )
            orderDao.insert(order.toEntity())
            cartService.clearCart()
            _orderState.value = UiState.Success(order.id)
        } catch (e: Exception) {
            _orderState.value = UiState.Error(e.message ?: "Ошибка при оформлении заказа")
        }
    }
}
