package com.app.core.repository

import com.app.core.repository.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface IUserRepository: CrudRepository<User, UUID> {
    fun findByEmail(email: String): User?
    fun findByUsername(username: String): User?
    fun findByExternalId(externalId: String): User?
}