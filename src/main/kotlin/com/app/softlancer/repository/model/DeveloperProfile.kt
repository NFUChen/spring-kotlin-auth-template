package com.app.softlancer.repository.model

import jakarta.persistence.*
import java.util.*

enum class WorkType(val type: String) {
    FullTime("FullTime"),
    PartTime("PartTime"),
    Internship("Internship")
}

@Entity
@Table(name = "developer_profile")
data class DeveloperProfile(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "skills", joinColumns = [JoinColumn(name = "user_id")])
    val skills: Set<String> = emptySet(),

    @Column(name = "work_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val workType: WorkType,

    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val portfolios: Set<Portfolio> = emptySet(),

    @OneToOne(mappedBy = "profile", fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    val user: User? = null,

    @Column(name = "description")
    val description: String? = null
)

@Entity
@Table(name = "portfolio")
data class Portfolio(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "description")
    val description: String? = null,

    @Column(name = "link", nullable = false)
    val link: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    val profile: DeveloperProfile? = null
)