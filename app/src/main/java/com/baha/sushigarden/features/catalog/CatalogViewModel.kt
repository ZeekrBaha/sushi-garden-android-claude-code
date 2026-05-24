package com.baha.sushigarden.features.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.models.Category
import com.baha.sushigarden.data.models.Product
import com.baha.sushigarden.data.services.cart.CartService
import com.baha.sushigarden.data.services.catalog.MenuRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val cartService: CartService
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("sushi")

    val uiState: StateFlow<UiState<List<Category>>> = flow {
        emit(UiState.Success(menuRepository.getCategories()))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UiState.Loading)

    val products: StateFlow<List<Product>> = _selectedCategory.map { catId ->
        menuRepository.getProducts(catId)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val cartItemCount: StateFlow<Int> = cartService.cartState
        .map { it.itemCount }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val selectedCategoryId: StateFlow<String> = _selectedCategory.asStateFlow()

    fun selectCategory(id: String) { _selectedCategory.value = id }
}
