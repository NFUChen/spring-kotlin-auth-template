package com.app.core

class SecurityException(message: String) : RuntimeException(message)

val UserNotFound = SecurityException("UserNotFound")
val PasswordNotMatch = SecurityException("PasswordNotMatch")