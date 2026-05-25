package com.baha.sushigarden.data.services.cart

import com.baha.sushigarden.data.models.AddOn
import com.baha.sushigarden.data.models.Product
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InMemoryCartServiceTest {

    private lateinit var service: InMemoryCartService

    private val product = Product("p1", "Хикари", 620, 255, "sushi", 0)
    private val addOnSauce   = AddOn("a1", "Соус", 50)
    private val addOnWasabi  = AddOn("a2", "Васаби", 30)

    @Before fun setUp() { service = InMemoryCartService() }

    @Test
    fun addItem_sameProductNoAddOns_mergesIntoOneLine() {
        service.addItem(product)
        service.addItem(product)
        val items = service.cartState.value.items
        assertEquals(1, items.size)
        assertEquals(2, items[0].quantity)
    }

    @Test
    fun addItem_sameProductSameAddOns_mergesIntoOneLine() {
        service.addItem(product, listOf(addOnSauce))
        service.addItem(product, listOf(addOnSauce))
        val items = service.cartState.value.items
        assertEquals(1, items.size)
        assertEquals(2, items[0].quantity)
    }

    @Test
    fun addItem_sameProductDifferentAddOns_createsSeparateLines() {
        service.addItem(product, listOf(addOnSauce))
        service.addItem(product, listOf(addOnWasabi))
        val items = service.cartState.value.items
        assertEquals(2, items.size)
        assertEquals(1, items[0].quantity)
        assertEquals(1, items[1].quantity)
    }

    @Test
    fun addItem_sameProductAddOnsInDifferentOrder_mergesIntoOneLine() {
        service.addItem(product, listOf(addOnSauce, addOnWasabi))
        service.addItem(product, listOf(addOnWasabi, addOnSauce))
        val items = service.cartState.value.items
        assertEquals(1, items.size)
        assertEquals(2, items[0].quantity)
    }

    @Test
    fun addItem_withQuantity_setsCorrectQuantity() {
        service.addItem(product, quantity = 3)
        val items = service.cartState.value.items
        assertEquals(1, items.size)
        assertEquals(3, items[0].quantity)
    }

    @Test
    fun removeItem_decrementsQuantity() {
        service.addItem(product, quantity = 2)
        service.removeItem(product.id)
        assertEquals(1, service.cartState.value.items[0].quantity)
    }

    @Test
    fun removeItem_atQuantityOne_removesLine() {
        service.addItem(product)
        service.removeItem(product.id)
        assertEquals(0, service.cartState.value.items.size)
    }

    @Test
    fun clearCart_emptiesAllLines() {
        service.addItem(product)
        service.clearCart()
        assertEquals(0, service.cartState.value.items.size)
    }
}
