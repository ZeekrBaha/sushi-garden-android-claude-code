package com.baha.sushigarden.features.productdetail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
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
class ProductDetailScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun productDetailDisplayed() {
        composeRule.onNodeWithTag("btn_add_to_cart").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("product_detail_default")
    }

    @Test
    fun stepperIncrement() {
        composeRule.onNodeWithTag("btn_increment").performClick()
        composeRule.onNodeWithTag("qty_label").assertTextEquals("2")
        composeRule.captureAndSaveScreenshot("product_detail_qty2")
    }

    @Test
    fun stepperDecrement_noLessThanOne() {
        composeRule.onNodeWithTag("btn_decrement").performClick()
        composeRule.onNodeWithTag("qty_label").assertTextEquals("1")
        composeRule.captureAndSaveScreenshot("product_detail_qty_min")
    }

    @Test
    fun addonSelection() {
        composeRule.onNodeWithTag("addon_ao1").onChildAt(0).performClick()
        composeRule.captureAndSaveScreenshot("product_detail_addon_selected")
        composeRule.onNodeWithTag("btn_add_to_cart").assertIsDisplayed()
    }

    @Test
    fun addToCart_navigatesBack() {
        composeRule.onNodeWithTag("btn_add_to_cart").performClick()
        composeRule.waitForIdle()
        composeRule.captureAndSaveScreenshot("catalog_after_add")
        composeRule.onNodeWithTag("catalog_address").assertIsDisplayed()
    }
}
