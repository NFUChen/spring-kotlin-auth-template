package com.app.softlancer.web.controller

import com.app.softlancer.repository.model.User
import com.app.softlancer.service.IAuthService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import java.util.*


data class UserCredentials(
    val email: String,
    val password: String
)

@RestController
@RequestMapping("/api")
class AuthController(
    val authService: IAuthService
) {

    data class UserIdentityLogin(val identityId: UUID)


    @GetMapping("/private/me")
    fun getCurrentUser(@AuthenticationPrincipal userDetails: User): ResponseEntity<UserDetails> {
        return ResponseEntity(userDetails, HttpStatus.OK)
    }

    @GetMapping("/private/logout")
    fun logout(
        @AuthenticationPrincipal userDetails: User,
        response: HttpServletResponse
    ): ResponseEntity<Map<String, String>> {
        authService.logout(response)
        return ResponseEntity.ok(mapOf("message" to "OK"))
    }

    @PostMapping("/public/login")
    fun login(
        @RequestBody credentials: UserCredentials,
        response: HttpServletResponse
    ): ResponseEntity<Map<String, String>> {
        val token = authService.login(credentials.email, credentials.password)
        authService.writeTokenToCookie(response, authService.LOGIN_KEY, token)
        return ResponseEntity.ok(mapOf("message" to "OK"))
    }

}