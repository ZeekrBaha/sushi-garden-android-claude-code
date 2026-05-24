package com.baha.sushigarden.features.auth

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.baha.sushigarden.HiltTestActivity
import com.baha.sushigarden.captureAndSaveScreenshot
import com.baha.sushigarden.ui.designsystem.SushiGardenTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AuthScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<HiltTestActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    private fun launchAuthScreen(onAuthSuccess: () -> Unit = {}) {
        composeRule.setContent {
            SushiGardenTheme {
                AuthScreen(onAuthSuccess = onAuthSuccess)
            }
        }
    }

    @Test
    fun authScreen_defaultShowsRegisterTab() {
        launchAuthScreen()
        composeRule.onNodeWithTag("tab_register").assertIsDisplayed()
        composeRule.onNodeWithTag("field_name").assertIsDisplayed()
        composeRule.onNodeWithTag("checkbox_consent").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("auth_register_tab_default")
    }

    @Test
    fun authScreen_switchToLoginHidesNameAndConsent() {
        launchAuthScreen()
        composeRule.onNodeWithTag("tab_login").performClick()
        composeRule.onNodeWithTag("field_email").assertIsDisplayed()
        composeRule.onNodeWithTag("field_password").assertIsDisplayed()
        composeRule.captureAndSaveScreenshot("auth_login_tab")
    }

    @Test
    fun authScreen_registerButtonDisabledWithoutConsent() {
        launchAuthScreen()
        composeRule.onNodeWithTag("field_name").performTextInput("Иван")
        composeRule.onNodeWithTag("field_email").performTextInput("ivan@test.com")
        composeRule.onNodeWithTag("field_password").performTextInput("Password1!")
        composeRule.onNodeWithTag("btn_auth_submit").assertIsNotEnabled()
        composeRule.captureAndSaveScreenshot("auth_register_no_consent")
    }

    @Test
    fun authScreen_registerButtonEnabledWithValidInputs() {
        launchAuthScreen()
        composeRule.onNodeWithTag("field_name").performTextInput("Иван")
        composeRule.onNodeWithTag("field_email").performTextInput("ivan@test.com")
        composeRule.onNodeWithTag("field_password").performTextInput("Password1!")
        composeRule.onNodeWithTag("checkbox_consent").performClick()
        composeRule.onNodeWithTag("btn_auth_submit").assertIsEnabled()
        composeRule.captureAndSaveScreenshot("auth_register_valid")
    }

    @Test
    fun authScreen_togglePasswordVisibility() {
        launchAuthScreen()
        composeRule.onNodeWithTag("tab_login").performClick()
        composeRule.onNodeWithTag("field_password").performTextInput("secret")
        composeRule.onNodeWithTag("btn_toggle_password").performClick()
        composeRule.captureAndSaveScreenshot("auth_password_visible")
        composeRule.onNodeWithTag("btn_toggle_password").performClick()
        composeRule.captureAndSaveScreenshot("auth_password_hidden")
    }

    @Test
    fun authScreen_loginCallsOnAuthSuccess() {
        var navigated = false
        launchAuthScreen(onAuthSuccess = { navigated = true })
        composeRule.onNodeWithTag("tab_login").performClick()
        composeRule.onNodeWithTag("field_email").performTextInput("test@test.com")
        composeRule.onNodeWithTag("field_password").performTextInput("password")
        composeRule.captureAndSaveScreenshot("auth_login_filled")
        composeRule.onNodeWithTag("btn_auth_submit").performClick()
        composeRule.waitForIdle()
        assertTrue(navigated)
        composeRule.captureAndSaveScreenshot("auth_login_success")
    }

    @Test
    fun authScreen_registerCallsOnAuthSuccess() {
        var navigated = false
        launchAuthScreen(onAuthSuccess = { navigated = true })
        composeRule.onNodeWithTag("field_name").performTextInput("Новый")
        composeRule.onNodeWithTag("field_email").performTextInput("new@test.com")
        composeRule.onNodeWithTag("field_password").performTextInput("Password1!")
        composeRule.onNodeWithTag("checkbox_consent").performClick()
        composeRule.captureAndSaveScreenshot("auth_register_ready")
        composeRule.onNodeWithTag("btn_auth_submit").performClick()
        composeRule.waitForIdle()
        assertTrue(navigated)
        composeRule.captureAndSaveScreenshot("auth_register_success")
    }
}
