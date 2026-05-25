package com.baha.sushigarden.features.auth

object AuthValidator {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(value: String): Boolean = emailRegex.matches(value.trim())

    fun isValidPassword(value: String): Boolean = value.length >= 6
}
