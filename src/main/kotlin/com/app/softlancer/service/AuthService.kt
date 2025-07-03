package com.app.softlancer.service

import com.app.softlancer.PasswordNotMatch
import com.app.softlancer.UserNotFound
import com.app.softlancer.config.WebProperties
import com.app.softlancer.repository.IUserRepository
import com.app.softlancer.repository.model.User
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrNull

interface IProvider {
    val name: String
}

interface IAuthService {
    val DefaultRoles: Iterable<String>
    val LOGIN_KEY: String
    fun assignRoles(userId: UUID, roles: Iterable<String>): User
    fun login(username: String, password: String): String
    fun userLogin(user: User): String
    fun writeTokenToCookie(response: HttpServletResponse, key: String, token: String)
    fun logout(response: HttpServletResponse)
    fun parseUserToken(token: String): User?
    fun isValidToken(token: String): Boolean
}

@Service
class AuthService(
    val webProperties: WebProperties,
    val userJwtService: UserJwtService,
    val userRepository: IUserRepository,
    val passwordEncoder: PasswordEncoder
) : IAuthService {

    override val DefaultRoles = setOf("ROLE_USER")

    @Transactional
    override fun assignRoles(userId: UUID, roles: Iterable<String>): User {
        val user = userRepository.findById(userId).getOrNull() ?: throw UserNotFound
        user.roles.clear()
        user.roles.addAll(roles.map { "ROLE_${it}" })
        val savedUser = userRepository.save(user)
        return savedUser
    }

    override val LOGIN_KEY = "jwt"

    override fun login(username: String, password: String): String {
        val user = userRepository.findByUsername(username) ?: throw UserNotFound
        if (!passwordEncoder.matches(password, user.password)) throw PasswordNotMatch

        return userJwtService.issueToken(user, webProperties.jwtValidSeconds)
    }

    override fun userLogin(user: User): String {
        return userJwtService.issueToken(user, webProperties.jwtValidSeconds)
    }

    override fun writeTokenToCookie(response: HttpServletResponse, key: String, token: String) {
        val cookie = Cookie(key, token)
        cookie.secure = true
        cookie.isHttpOnly = true
        cookie.path = "/"
        response.addCookie(cookie)
    }

    override fun logout(response: HttpServletResponse) {
        val cookie = Cookie(LOGIN_KEY, null)
        cookie.secure = true
        cookie.isHttpOnly = true
        cookie.path = "/"
        cookie.maxAge = 0
        response.addCookie(cookie)
    }

    override fun parseUserToken(token: String): User? {
        return userJwtService.parseToken(token)
    }

    override fun isValidToken(token: String): Boolean {
        return userJwtService.isValidToken(token)
    }
}