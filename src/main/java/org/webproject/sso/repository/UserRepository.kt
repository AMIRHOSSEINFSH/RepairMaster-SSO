package org.webproject.sso.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.webproject.sso.model.dao.user.User
import java.util.*

interface UserRepository: JpaRepository<User, UUID> {

    fun findUserByEmail(email: String): User?

}