package com.baha.sushigarden.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.data.models.Order
import com.baha.sushigarden.data.models.UserProfile
import com.baha.sushigarden.data.services.auth.AuthService
import com.baha.sushigarden.data.services.orders.OrderDao
import com.baha.sushigarden.data.services.orders.toOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authService: AuthService,
    private val orderDao: OrderDao
) : ViewModel() {

    val user: StateFlow<UserProfile?> = authService.currentUser
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val orders: StateFlow<List<Order>> = orderDao.getAll()
        .map { it.map { e -> e.toOrder() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun logout() = viewModelScope.launch { authService.signOut() }
}
