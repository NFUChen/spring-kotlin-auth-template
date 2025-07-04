package com.app.softlancer.repository.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*


enum class Role(name: String) {
    Admin("ROLE_ADMIN"),
    User("ROLE_USER"),
    Guest("ROLE_GUEST")
}

data class UserView(
    val id: UUID,
    val userId: UUID,
    val roles: Set<String>,
)


@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID?,

    @Column(nullable = false, unique = true)
    private val username: String,

    @Column(nullable = true)
    @JsonIgnore
    private val password: String?,

    @Column(nullable = false)
    val email: String,

    @Column(nullable = false)
    val provider: String,

    @Column(nullable = true)
    val externalId: String?,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    val roles: MutableSet<String> = mutableSetOf(),


    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "profile_id", referencedColumnName = "id", nullable = true)
    val profile: DeveloperProfile? = null, // nullable to allow users without a profile, since user may not be a developer

    ) : UserDetails {

    companion object {
        const val DefaultPlatform = "LOCAL"
    }

    constructor(name: String, email: String,password: String, provider: String, externalId: String?, roles: Iterable<String>) : this(
        id = null,
        username = name,
        password = password,
        roles = roles.toMutableSet(),
        provider = provider,
        externalId = externalId,
        email = email
    )

    @JsonIgnore
    override fun getAuthorities(): Collection<GrantedAuthority> = roles.map { GrantedAuthority { it } }

    override fun getPassword(): String = password ?: ""

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}