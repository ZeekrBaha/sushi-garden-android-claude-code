package com.baha.sushigarden.features.promotions

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToIndex
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class PromotionsScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        composeRule.onNodeWithTag("nav_promos").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun promoBannersRender() {
        composeRule.onNodeWithTag("promos_list").assertIsDisplayed()
        composeRule.onNodeWithTag("promo_p1").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("promotions")
    }

    @Test
    fun promoScrollable() {
        composeRule.onNodeWithTag("promos_list").performScrollToIndex(2)
        composeRule.onNodeWithTag("promo_p3").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("promotions_scrolled")
    }
}
