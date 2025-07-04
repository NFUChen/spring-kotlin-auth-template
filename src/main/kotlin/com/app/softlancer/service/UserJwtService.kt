package com.app.softlancer.service

import com.app.softlancer.repository.model.User
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.*


@Service
class UserJwtService(
    val webProperties: WebProperties,
    val defaultJwtService: DefaultJwtService,
) : IJwtService<User> {
    override lateinit var secret: ByteArray

    @PostConstruct
    fun postConstruct() {
        secret = Base64.getDecoder().decode(webProperties.jwtSecret)
    }

    override fun issueToken(
        claims: User,
        expireAtNumberOfSeconds: Int
    ): String {
        val externalId = claims.externalId ?: ""
        val mapClaims = mutableMapOf(
            "id" to claims.id!!,
            "email" to claims.email,
            "username" to claims.username,
            "roles" to claims.roles,
            "provider" to claims.provider,
            "externalId" to externalId,
        )

        return defaultJwtService.issueToken(mapClaims, expireAtNumberOfSeconds)
    }

    override fun parseToken(token: String): User? {
        val claims = defaultJwtService.parseToken(token) ?: return null
        return User(
            id = UUID.fromString(claims["sub"] as String),
            email = claims["email"] as String,
            username = claims["username"] as String,
            roles = (claims["roles"] as List<*>).map { it.toString() }.toMutableSet(),
            password = "(sensitive)",
            provider = claims["provider"] as String,
            externalId = claims["externalId"] as String,
        )
    }

    override fun isValidToken(token: String): Boolean {
        return defaultJwtService.isValidToken(token)
    }
}