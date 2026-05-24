package com.baha.sushigarden.data.services.auth

import com.baha.sushigarden.data.models.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthService : AuthService {
    private val auth = FirebaseAuth.getInstance()

    override val currentUser: Flow<UserProfile?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { a ->
            val u = a.currentUser
            trySend(u?.let { UserProfile(it.uid, it.displayName ?: "", it.email ?: "") })
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signUp(email: String, password: String, name: String): UserProfile {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user!!
        user.updateProfile(userProfileChangeRequest { displayName = name }).await()
        return UserProfile(user.uid, name, email)
    }

    override suspend fun signIn(email: String, password: String): UserProfile {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user!!
        return UserProfile(user.uid, user.displayName ?: "", user.email ?: "")
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}
