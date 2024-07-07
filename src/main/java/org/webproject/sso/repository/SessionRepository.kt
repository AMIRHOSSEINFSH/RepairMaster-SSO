package org.webproject.sso.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.webproject.sso.model.dao.user.Session
import java.util.UUID

@Repository
interface SessionRepository: JpaRepository<Session, UUID> {

    fun findByUserIdAndDeviceOwner(userId: UUID, deviceOwner: String): Session?

}