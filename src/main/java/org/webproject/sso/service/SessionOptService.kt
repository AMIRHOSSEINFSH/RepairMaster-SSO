package org.webproject.sso.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.webproject.sso.model.config.AppConfig
import org.webproject.sso.repository.SessionRepository
import org.webproject.sso.repository.UserRepository
import java.time.LocalDateTime

@Service
class SessionOptService constructor(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val sessionRepository: SessionRepository,
    @Autowired private val appConfig: AppConfig
) {

    @Scheduled(fixedRate = 15*60*1000)
    fun performOptimization() {
        val userList= userRepository.findUsersByCreatedAtAfterAndTokenCount(LocalDateTime.now().minusMinutes(15),-1)

        val sessionList=  sessionRepository.findAllById(userList.map { it?.id })

        sessionRepository.deleteAll(sessionList)
        userRepository.deleteAll(userList)
        println("Users with ids: ${userList.map { it?.id }} Have been removed due to Scheduler time interval of 15 min")
    }

}