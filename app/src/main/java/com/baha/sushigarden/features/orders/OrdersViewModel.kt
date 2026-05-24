package com.baha.sushigarden.features.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.data.models.Order
import com.baha.sushigarden.data.services.orders.OrderDao
import com.baha.sushigarden.data.services.orders.toOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(private val orderDao: OrderDao) : ViewModel() {

    val orders: StateFlow<List<Order>> = orderDao.getAll()
        .map { entities -> entities.map { it.toOrder() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun getOrder(id: String) = orders.value.find { it.id == id }
}
