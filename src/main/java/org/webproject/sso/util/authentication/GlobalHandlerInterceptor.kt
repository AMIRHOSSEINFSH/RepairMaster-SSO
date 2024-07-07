package org.webproject.sso.util.authentication

import com.google.gson.Gson
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.webproject.responsewrapper.custom.const.DEVICE_MODEL_HEADER
import org.webproject.responsewrapper.custom.const.HASH_HEADER
import org.webproject.responsewrapper.custom.const.NONCE_HEADER
import org.webproject.responsewrapper.custom.const.TOKEN_HEADER
import org.webproject.responsewrapper.custom.exception.DefaultSupportedException
import org.webproject.responsewrapper.custom.response.ResponseHandler
import org.webproject.sso.authentication.KeyExchange
import org.webproject.sso.authentication.KeyExchange.Companion.makeHmac
import org.webproject.sso.authentication.TokenManagement.parseToken
import org.webproject.sso.repository.SessionRepository
import org.webproject.sso.util.annotations.FreeAuthentication
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.stream.Collectors

@Component
class GlobalHandlerInterceptor @Autowired constructor(
    private val sessionRepository: SessionRepository,
    private val exchange: KeyExchange
): HandlerInterceptor {


    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val deviceModel=  request.getHeader(DEVICE_MODEL_HEADER) ?: getErrorResponse(response).also { return false }
        if(handler is HandlerMethod) {
            if (!handler.hasMethodAnnotation(FreeAuthentication::class.java)) {
                request.getHeader(TOKEN_HEADER)?.let { itToken->
                    val token = parseToken(itToken)
                    request.setAttribute("token",token)
                    val isNotExpired = LocalDateTime.now().isBefore(LocalDateTime.ofInstant(Instant.ofEpochSecond(token.lifeTime), ZoneId.systemDefault()))
                    if(!isNotExpired) getErrorResponse(response).also { return false }

                    //TODO client is not ready for hmac integrity ?
                    /*val hashMsg = request.getHeader(HASH_HEADER)
                    val nonce = request.getHeader(NONCE_HEADER)?.toLongOrNull()
                    if(nonce != null && hashMsg != null) {
                        val requestBody = getRequestBody(request)
                        val uSession=  sessionRepository.findByUserIdAndDeviceOwner(token.sessionId,deviceModel) ?: throw DefaultSupportedException("UnAuthorized",403)
                        val serverPrK = uSession.server_secret
                        val userPuK = uSession.user_rik
                        val sharedKey = exchange.generateKey(serverPrK,userPuK)
                        val expectedMac = makeHmac(requestBody,sharedKey)
                        if(hashMsg != expectedMac) throw DefaultSupportedException("Your Have Manipulated Data Integrity!!",403)
                    }*/

                }?: getErrorResponse(response).also { return false }

            }
        }
        return true;

    }

    private fun getRequestBody(request: HttpServletRequest): String {
        val wrapper = request as CachedBodyHttpServletRequest
        val buf = wrapper.reader.lines().collect(Collectors.joining())
        return buf
    }


    private fun getErrorResponse(response: HttpServletResponse) {
        val jsonResult = Gson().toJson(ResponseHandler.generateResponse("Forbidden",403,null).body)
        response.writer.write(jsonResult)
        response.writer.flush()
    }

}