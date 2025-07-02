package com.app.softlancer.repository.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*


@Entity
@Table(name = "project_profile")
data class ProjectProfile(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    val description: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "required_skills", joinColumns = [JoinColumn(name = "project_id")])
    @Column(name = "skill")
    val requiredSkills: Set<String> = emptySet(),

    @Column(name = "work_type", nullable = false)
    @Enumerated(EnumType.STRING)
    val workType: WorkType,

    @Column(name = "duration_weeks")
    val estimatedDurationWeeks: Int? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    val client: User
)