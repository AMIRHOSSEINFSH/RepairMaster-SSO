package org.webproject.sso.util.authentication

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.webproject.responsewrapper.custom.const.DEVICE_MODEL_HEADER
import org.webproject.responsewrapper.custom.const.TOKEN_HEADER
import org.webproject.sso.authentication.TokenManagement.parseToken
import org.webproject.sso.util.annotations.FreeAuthentication
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Component
class GlobalHandlerInterceptor: HandlerInterceptor {


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val deviceModel=  request.getHeader(DEVICE_MODEL_HEADER) ?: return false
        if(handler is HandlerMethod) {
            if (!handler.hasMethodAnnotation(FreeAuthentication::class.java)) {
                request.getHeader(TOKEN_HEADER)?.let { itToken->
                    val token = parseToken(itToken)
                    request.setAttribute("token",token)
                    val isNotExpired = LocalDateTime.now().isBefore(LocalDateTime.ofInstant(Instant.ofEpochSecond(token.lifeTime), ZoneId.systemDefault()))
                    if(!isNotExpired) return false

                    /*val hashMsg = request.getHeader(HASH_HEADER)
                    val nonce = request.getHeader(NONCE_HEADER)
                    val body = request
                    checkHashingIntegrity(hashMsg,nonce.toLong(),body)*/
                }?: return false
                //TODO Check also integrity

            }
        }
        return true;

    }

    private fun checkHashingIntegrity(hashData: String,nonce: Long, body: String): Boolean {

        return true
    }

}