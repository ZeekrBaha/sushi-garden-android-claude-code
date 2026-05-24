package com.baha.sushigarden.features.auth

import com.baha.sushigarden.data.models.UserProfile
import com.baha.sushigarden.data.services.auth.FakeAuthService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FakeAuthServiceTest {
    private val user = UserProfile("uid1", "Иван", "ivan@test.com")
    private val svc  = FakeAuthService(preSeededUser = user)

    @Test fun currentUser_returnsSeededUser() = runTest {
        assertEquals(user, svc.currentUser.first())
    }

    @Test fun signIn_succeeds() = runTest {
        val result = svc.signIn("ivan@test.com", "password")
        assertEquals(user, result)
    }

    @Test fun signOut_clearsUser() = runTest {
        svc.signOut()
        assertNull(svc.currentUser.first())
    }

    @Test fun signUp_returnsNewUser() = runTest {
        val fresh = FakeAuthService()
        val result = fresh.signUp("new@test.com", "pass123", "Новый")
        assertEquals("Новый", result.name)
        assertEquals("new@test.com", result.email)
    }
}
