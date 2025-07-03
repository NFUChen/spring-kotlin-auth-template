package com.app.softlancer

class SecurityException(message: String) : RuntimeException(message)

val UserNotFound = SecurityException("UserNotFound")
val PasswordNotMatch = SecurityException("PasswordNotMatch")