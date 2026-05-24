package com.baha.sushigarden.features.profile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import com.baha.sushigarden.MainActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class ProfileScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
        composeRule.onNodeWithTag("nav_profile").performClick()
        composeRule.waitForIdle()
    }

    @Test
    fun profileInfoVisible() {
        composeRule.onNodeWithTag("profile_name").assertIsDisplayed()
        composeRule.onNodeWithTag("profile_email").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("profile")
    }

    @Test
    fun phoneFieldEditable() {
        composeRule.onNodeWithTag("profile_phone").performTextClearance()
        composeRule.onNodeWithTag("profile_phone").performTextInput("+7 999 999-99-99")
        composeRule.onNodeWithTag("profile_phone").assertTextContains("+7 999 999-99-99")
        composeRule.captureAndSaveScreenshot("profile_phone_edited")
    }

    @Test
    fun logoutNavigatesToAuth() {
        composeRule.onNodeWithTag("btn_logout").performClick()
        composeRule.waitForIdle()
        composeRule.captureAndSaveScreenshot("auth_after_logout")
        composeRule.onNodeWithTag("btn_auth_submit").assertIsDisplayed()
    }
}
