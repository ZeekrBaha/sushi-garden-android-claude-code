package com.baha.sushigarden.data.services.auth

import com.baha.sushigarden.data.models.UserProfile
import kotlinx.coroutines.flow.Flow

interface AuthService {
    val currentUser: Flow<UserProfile?>
    suspend fun signUp(email: String, password: String, name: String): UserProfile
    suspend fun signIn(email: String, password: String): UserProfile
    suspend fun signOut()
}
