package com.app.softlancer.repository

import com.app.softlancer.repository.model.DeveloperProfile
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface IDeveloperProfileRepository: CrudRepository<DeveloperProfile, UUID>
