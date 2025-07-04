package com.app.softlancer.service

import com.app.softlancer.repository.model.Role
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component


@ConfigurationProperties(prefix = "app.admin")
class AdminProperties {
    lateinit var username: String
    lateinit var password: String
    lateinit var email: String
}

@Component
class SystemInitService(
    private val adminProperties: AdminProperties,
    private val registrationService: RegistrationService
) {

    private val logger = LoggerFactory.getLogger(SystemInitService::class.java)

    @PostConstruct
    @Transactional
    fun init() {
        // Initialize the system, e.g., create default admin user if it doesn't exist
        this.initSystemUser()
    }
    /**
     * Initializes the system user with admin privileges.
     * This method is called during application startup to ensure that
     * there is always an admin user available.
     */
    fun initSystemUser() {
        logger.info("Initializing system user with admin privileges...")
        this.registrationService.registerUser(
            username = adminProperties.username,
            password = adminProperties.password,
            email = adminProperties.email,
            roles = setOf(Role.Admin.name)
        )
    }
}