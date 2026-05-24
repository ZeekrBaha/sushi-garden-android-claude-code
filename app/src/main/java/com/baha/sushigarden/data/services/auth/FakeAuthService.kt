package com.baha.sushigarden.data.services.auth

import com.baha.sushigarden.data.models.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeAuthService(preSeededUser: UserProfile? = null) : AuthService {
    private val _user = MutableStateFlow(preSeededUser)
    override val currentUser: Flow<UserProfile?> = _user

    override suspend fun signUp(email: String, password: String, name: String): UserProfile {
        val user = UserProfile(uid = "fake-uid", name = name, email = email)
        _user.value = user
        return user
    }

    override suspend fun signIn(email: String, password: String): UserProfile {
        val user = _user.value ?: UserProfile("fake-uid", "Тест", email)
        _user.value = user
        return user
    }

    override suspend fun signOut() {
        _user.value = null
    }
}
