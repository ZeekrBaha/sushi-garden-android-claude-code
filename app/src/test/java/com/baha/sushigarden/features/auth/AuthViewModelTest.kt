package com.baha.sushigarden.features.auth

import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.services.auth.FakeAuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var vm: AuthViewModel

    @Before fun setUp() {
        Dispatchers.setMain(dispatcher)
        vm = AuthViewModel(FakeAuthService())
    }

    @After fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test fun register_updatesStateToSuccess() = runTest(dispatcher) {
        vm.onNameChange("Иван")
        vm.onEmailChange("ivan@test.com")
        vm.onPasswordChange("Password1!")
        vm.onConsentChange(true)
        vm.register()
        assertTrue(vm.uiState.value is UiState.Success)
    }

    @Test fun register_requiresConsent() = runTest(dispatcher) {
        vm.onNameChange("Иван")
        vm.onEmailChange("ivan@test.com")
        vm.onPasswordChange("Password1!")
        vm.onConsentChange(false)
        assertFalse(vm.canRegister.value)
    }

    @Test fun register_requiresValidEmail() = runTest(dispatcher) {
        vm.onNameChange("Иван")
        vm.onEmailChange("ivan@localhost")
        vm.onPasswordChange("Password1!")
        vm.onConsentChange(true)
        assertFalse(vm.canRegister.value)
    }

    @Test fun register_rejectsEmailWithoutDomain() = runTest(dispatcher) {
        vm.onNameChange("Иван")
        vm.onEmailChange("ivan@")
        vm.onPasswordChange("Password1!")
        vm.onConsentChange(true)
        assertFalse(vm.canRegister.value)
    }

    @Test fun register_requiresMinPasswordLength() = runTest(dispatcher) {
        vm.onNameChange("Иван")
        vm.onEmailChange("ivan@test.com")
        vm.onPasswordChange("123")
        vm.onConsentChange(true)
        assertFalse(vm.canRegister.value)
    }

    @Test fun login_updatesStateToSuccess() = runTest(dispatcher) {
        vm.onEmailChange("test@test.com")
        vm.onPasswordChange("password")
        vm.login()
        assertTrue(vm.uiState.value is UiState.Success)
    }

    @Test fun login_requiresValidEmail() = runTest(dispatcher) {
        vm.onEmailChange("test@localhost")
        vm.onPasswordChange("password")
        assertFalse(vm.canLogin.value)
    }

    @Test fun togglePassword_flipsVisibility() = runTest(dispatcher) {
        assertFalse(vm.showPassword.value)
        vm.togglePasswordVisibility()
        assertTrue(vm.showPassword.value)
    }
}
