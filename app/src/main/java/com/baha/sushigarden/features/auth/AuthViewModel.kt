package com.baha.sushigarden.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baha.sushigarden.UiState
import com.baha.sushigarden.data.models.UserProfile
import com.baha.sushigarden.data.services.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authService: AuthService) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authService.currentUser
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val uiState = MutableStateFlow<UiState<UserProfile>>(UiState.Idle)

    val name     = MutableStateFlow("")
    val email    = MutableStateFlow("")
    val password = MutableStateFlow("")
    val consent  = MutableStateFlow(false)
    val showPassword = MutableStateFlow(false)

    val canRegister: StateFlow<Boolean> = combine(name, email, password, consent) { n, e, p, c ->
        n.isNotBlank() && e.contains("@") && p.length >= 6 && c
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun onNameChange(v: String)     { name.value = v }
    fun onEmailChange(v: String)    { email.value = v }
    fun onPasswordChange(v: String) { password.value = v }
    fun onConsentChange(v: Boolean) { consent.value = v }
    fun togglePasswordVisibility()  { showPassword.value = !showPassword.value }

    fun register() = viewModelScope.launch {
        uiState.value = UiState.Loading
        uiState.value = try {
            UiState.Success(authService.signUp(email.value, password.value, name.value))
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Ошибка регистрации")
        }
    }

    fun login() = viewModelScope.launch {
        uiState.value = UiState.Loading
        uiState.value = try {
            UiState.Success(authService.signIn(email.value, password.value))
        } catch (e: Exception) {
            UiState.Error(e.message ?: "Неверные данные")
        }
    }
}
