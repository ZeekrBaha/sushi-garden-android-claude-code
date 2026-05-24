package com.baha.sushigarden.features.tracking

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
class TrackingScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        // Place an order to land on TrackingScreen
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
    }

    @Test
    fun trackingMapVisible() {
        composeRule.onNodeWithTag("tracking_map").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("tracking_map")
    }

    @Test
    fun etaCardVisible() {
        composeRule.onNodeWithTag("tracking_eta_card").assertIsDisplayed()
        composeRule.onNodeWithTag("tracking_eta_label").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("tracking_eta")
    }

    @Test
    fun progressBarVisible() {
        composeRule.onNodeWithTag("tracking_progress").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("tracking_progress")
    }

    @Test
    fun backButtonNavigatesUp() {
        composeRule.onNodeWithTag("btn_back_tracking").performClick()
        composeRule.waitForIdle()
        composeRule.captureAndSaveScreenshot("tracking_back_pressed")
        // After navigating up from tracking, should be at checkout or catalog
        // Assert the back button itself is gone (we left tracking screen)
        composeRule.onNodeWithTag("tracking_map").assertDoesNotExist()
    }
}
