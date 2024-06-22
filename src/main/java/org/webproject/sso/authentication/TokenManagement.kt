package org.webproject.sso.authentication

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

object TokenManagement {

    data class Token(val lifeTime: Long,val sessionId: UUID,val deviceModel: String,val payload: Any?=null)

    fun generateToken(sessionId: UUID,type: TokenType,deviceModel: String,payload: Any?=null): String {
        val data = Token(LocalDateTime.now().plusSeconds(type.time).toEpochSecond(ZoneOffset.UTC),sessionId,deviceModel,payload)
        return CryptoHelper.encrypt(data)
    }

    fun parseToken(token: String): Token {
        return CryptoHelper.decrypt(token,Token::class.java)
    }


}

enum class TokenType(val time: Long) {
    VERIFY(900),
    SYSTEM(86400)
}