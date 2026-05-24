package com.baha.sushigarden.features.checkout

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
class CheckoutScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nav_cart").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("btn_checkout").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun checkoutFieldsDisplayed() {
        composeRule.onNodeWithTag("field_street").assertIsDisplayed()
        composeRule.onNodeWithTag("field_recipient").assertIsDisplayed()
        composeRule.onNodeWithTag("field_phone").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("checkout_empty")
    }

    @Test
    fun confirmCreatesOrderAndNavigatesToTracking() {
        composeRule.onNodeWithTag("field_street").performTextInput("ул. Пушкина, 10")
        composeRule.onNodeWithTag("field_recipient").performTextInput("Иван Иванов")
        composeRule.onNodeWithTag("field_phone").performTextInput("+7 900 000-00-00")
        composeRule.onNodeWithTag("field_email").performTextInput("test@test.com")
        composeRule.captureAndSaveScreenshot("checkout_filled")
        composeRule.onNodeWithTag("btn_confirm").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("tracking_map").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("tracking_after_checkout")
    }
}
