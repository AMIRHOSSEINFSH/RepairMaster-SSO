package org.webproject.sso.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.webproject.sso.model.dao.user.User
import java.time.LocalDateTime
import java.util.*


interface UserRepository: JpaRepository<User, UUID> {

    fun findUserByEmail(email: String): User?

//    fun findByCreatedAtAfterAndTokenCount(createdAt: LocalDateTime?, tokenCount: Int): List<User?>?

    @Query("SELECT u FROM User_tbl u WHERE u.created_at > :createdAt AND u.token_count = :tokenCount")
    fun findUsersByCreatedAtAfterAndTokenCount(
        @Param("createdAt") createdAt: LocalDateTime?,
        @Param("tokenCount") tokenCount: Int
    ): List<User?>

//    fun findByCreated_atAfterAndToken_countIs(createdAt: LocalDateTime?, tokenCount: Int): List<User>
}