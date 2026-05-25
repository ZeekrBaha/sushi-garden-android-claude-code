package com.baha.sushigarden.features.checkout

import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.models.Product
import com.baha.sushigarden.data.services.cart.InMemoryCartService
import com.baha.sushigarden.data.services.orders.OrderDao
import com.baha.sushigarden.data.services.orders.OrderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CheckoutViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private val fakeDao = object : OrderDao {
        override suspend fun insert(order: OrderEntity) {}
        override suspend fun getById(id: String): OrderEntity? = null
        override fun getAll(): Flow<List<OrderEntity>> = flowOf(emptyList())
    }

    private val product = Product("p1", "Хикари", 620, 255, "sushi", 0)

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After  fun tearDown() { Dispatchers.resetMain() }

    private fun buildVm(cart: InMemoryCartService = InMemoryCartService()) =
        Pair(cart, CheckoutViewModel(cart, fakeDao))

    @Test
    fun canConfirm_falseWhenCartEmpty() = runTest(dispatcher) {
        val (_, vm) = buildVm()
        vm.street.value    = "ул. Пушкина, 1"
        vm.recipient.value = "Иван"
        vm.phone.value     = "+7 900 000-00-00"
        vm.email.value     = "test@test.com"
        assertFalse(vm.canConfirm.value)
    }

    @Test
    fun canConfirm_falseWhenFieldsBlank() = runTest(dispatcher) {
        val (cart, vm) = buildVm()
        cart.addItem(product)
        assertFalse(vm.canConfirm.value)
    }

    @Test
    fun canConfirm_falseWhenEmailInvalid() = runTest(dispatcher) {
        val (cart, vm) = buildVm()
        cart.addItem(product)
        vm.street.value    = "ул. Пушкина, 1"
        vm.recipient.value = "Иван"
        vm.phone.value     = "+7 900 000-00-00"
        vm.email.value     = "notanemail"
        assertFalse(vm.canConfirm.value)
    }

    @Test
    fun canConfirm_trueWhenAllFieldsFilledAndCartNonEmpty() = runTest(dispatcher) {
        val (cart, vm) = buildVm()
        cart.addItem(product)
        vm.street.value    = "ул. Пушкина, 1"
        vm.recipient.value = "Иван"
        vm.phone.value     = "+7 900 000-00-00"
        vm.email.value     = "test@test.com"
        assertTrue(vm.canConfirm.value)
    }

    @Test
    fun confirm_withEmptyCart_emitsError() = runTest(dispatcher) {
        val (_, vm) = buildVm()
        vm.confirm()
        assertTrue(vm.orderState.value is UiState.Error)
    }

    @Test
    fun confirm_withValidData_emitsSuccess() = runTest(dispatcher) {
        val (cart, vm) = buildVm()
        cart.addItem(product)
        vm.street.value    = "ул. Пушкина, 1"
        vm.recipient.value = "Иван"
        vm.phone.value     = "+7 900 000-00-00"
        vm.email.value     = "test@test.com"
        vm.confirm()
        assertTrue(vm.orderState.value is UiState.Success)
    }

    @Test
    fun confirm_clearsCartOnSuccess() = runTest(dispatcher) {
        val (cart, vm) = buildVm()
        cart.addItem(product)
        vm.street.value    = "ул. Пушкина, 1"
        vm.recipient.value = "Иван"
        vm.phone.value     = "+7 900 000-00-00"
        vm.email.value     = "test@test.com"
        vm.confirm()
        assertTrue(cart.cartState.value.items.isEmpty())
    }
}
