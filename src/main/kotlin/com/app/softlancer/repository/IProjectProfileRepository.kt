package com.app.softlancer.repository

import com.app.softlancer.repository.model.ProjectProfile
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface IProjectProfileRepository: CrudRepository<ProjectProfile, UUID>