package com.baha.sushigarden.features.orders

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class OrdersScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        composeRule.onNodeWithTag("nav_orders").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun emptyOrdersState() {
        composeRule.onNodeWithTag("orders_empty").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("orders_empty")
    }

    @Test
    fun filledOrdersState() {
        // Navigate to catalog first (Before lands on Orders tab)
        composeRule.onNodeWithTag("nav_catalog").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nav_cart").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_checkout").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("field_street").performTextInput("ул. Ленина, 1")
        composeRule.onNodeWithTag("field_recipient").performTextInput("Тест")
        composeRule.onNodeWithTag("field_phone").performTextInput("+7 900 000-00-00")
        composeRule.onNodeWithTag("field_email").performTextInput("t@t.com")
        composeRule.onNodeWithTag("btn_confirm").performClick()
        composeRule.waitForIdle()
        // Navigate to Orders tab
        composeRule.onNodeWithTag("nav_orders").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("orders_list").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("orders_filled_list")
    }
}
