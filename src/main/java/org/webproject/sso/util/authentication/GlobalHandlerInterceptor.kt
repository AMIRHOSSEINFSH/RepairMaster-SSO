package org.webproject.sso.util.authentication

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.webproject.sso.authentication.TokenManagement.parseToken
import org.webproject.sso.util.DEVICE_MODEL_HEADER
import org.webproject.sso.util.TOKEN_HEADER
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
                    return LocalDateTime.now().isBefore(LocalDateTime.ofInstant(Instant.ofEpochSecond(token.lifeTime), ZoneId.systemDefault()))
                }?: return false
                //TODO Check also integrity
            }
        }
        return true;

    }

}