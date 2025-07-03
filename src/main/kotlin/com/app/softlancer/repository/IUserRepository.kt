package com.app.softlancer.repository

import com.app.softlancer.repository.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface IUserRepository: CrudRepository<User, UUID> {
    fun findByUsername(username: String): User?
}