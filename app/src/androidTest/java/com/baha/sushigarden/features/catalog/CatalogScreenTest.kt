package com.baha.sushigarden.features.catalog

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
class CatalogScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun addressHeaderVisible() {
        composeRule.onNodeWithTag("catalog_address").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("catalog_default")
    }

    @Test
    fun categoriesDisplay() {
        composeRule.onNodeWithTag("catalog_categories").assertIsDisplayed()
        composeRule.onNodeWithTag("category_sushi").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("catalog_categories")
    }

    @Test
    fun categorySwitch_rollsCategory() {
        composeRule.onNodeWithTag("category_rolls").performClick()
        composeRule.waitForIdle()
        composeRule.captureAndSaveScreenshot("catalog_rolls_selected")
        composeRule.onNodeWithTag("catalog_grid").assertIsDisplayed()
    }

    @Test
    fun tapProductNavigatesToDetail() {
        composeRule.onNodeWithTag("product_p1").performClick()
        composeRule.waitForIdle()
        composeRule.captureAndSaveScreenshot("product_detail_p1")
        composeRule.onNodeWithTag("btn_add_to_cart").assertIsDisplayed()
    }
}
