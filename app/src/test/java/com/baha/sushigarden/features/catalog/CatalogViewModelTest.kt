package com.baha.sushigarden.features.catalog

import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.services.cart.InMemoryCartService
import com.baha.sushigarden.data.services.catalog.LocalMenuRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val repo = LocalMenuRepository()
    private val cart = InMemoryCartService()
    private lateinit var vm: CatalogViewModel

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        vm = CatalogViewModel(repo, cart)
    }

    @After fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test fun initialState_loadsCategories() = runTest(dispatcher) {
        assertTrue(vm.uiState.value is UiState.Success)
        assertEquals(5, (vm.uiState.value as UiState.Success).data.size)
    }

    @Test fun selectCategory_filtersProducts() = runTest(dispatcher) {
        vm.selectCategory("rolls")
        assertTrue(vm.products.value.isNotEmpty())
        assertTrue(vm.products.value.all { it.categoryId == "rolls" })
    }

    @Test fun cartBadge_updatesOnAdd() = runTest(dispatcher) {
        val product = repo.getAllProducts().first()
        cart.addItem(product)
        assertEquals(1, vm.cartItemCount.first())
    }
}
