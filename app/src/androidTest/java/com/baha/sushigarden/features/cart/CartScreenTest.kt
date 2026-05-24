package com.baha.sushigarden.features.cart

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test



@HiltAndroidTest
class CartScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    private fun navigateToCart() {
        composeRule.onNodeWithTag("nav_cart").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun emptyCartShowsEmptyState() {
        navigateToCart()
        composeRule.onNodeWithTag("cart_empty").assertIsDisplayed()
        composeRule.onNodeWithTag("btn_checkout").assertIsNotEnabled()
        composeRule.captureAndSaveScreenshot("cart_empty")
    }

    @Test
    fun filledCart_itemVisible() {
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        navigateToCart()
        composeRule.onNodeWithTag("cart_item_p1").assertIsDisplayed()
        composeRule.onNodeWithTag("btn_checkout").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("cart_filled")
    }

    @Test
    fun cartItemQtyIncrement() {
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        navigateToCart()
        composeRule.onNodeWithTag("cart_increment_p1").performClick()
        composeRule.onNodeWithTag("cart_qty_p1").assertTextEquals("2")
        composeRule.captureAndSaveScreenshot("cart_qty_incremented")
    }

    @Test
    fun cartItemQtyDecrement() {
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        navigateToCart()
        composeRule.onNodeWithTag("cart_increment_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("cart_qty_p1").assertTextEquals("2")
        composeRule.onNodeWithTag("cart_decrement_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("cart_qty_p1").assertTextEquals("1")
        composeRule.captureAndSaveScreenshot("cart_qty_decremented")
    }

    @Test
    fun cartItemRemovedOnDecrementToZero() {
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        navigateToCart()
        composeRule.onNodeWithTag("cart_item_p1").assertIsDisplayed()
        composeRule.onNodeWithTag("cart_decrement_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("cart_empty").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("cart_item_removed")
    }

    @Test
    fun checkoutButton_navigatesToCheckout() {
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        navigateToCart()
        composeRule.onNodeWithTag("btn_checkout").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("field_street").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("cart_to_checkout")
    }
}
