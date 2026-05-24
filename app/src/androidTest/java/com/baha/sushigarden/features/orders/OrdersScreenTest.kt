package com.baha.sushigarden.features.orders

import androidx.compose.ui.test.assertIsDisplayed
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
}
