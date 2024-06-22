package org.webproject.sso.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.webproject.sso.authentication.KeyExchange
import org.webproject.sso.model.dto.UserDto
import org.webproject.sso.model.dto.otp.OtpRequestModel
import org.webproject.sso.model.dto.response.AuthenticationResponseModel
import org.webproject.sso.model.dto.response.RegisterUserResponse
import org.webproject.sso.service.UserService
import org.webproject.sso.util.DEVICE_MODEL_HEADER
import org.webproject.sso.util.annotations.FreeAuthentication
import org.webproject.sso.util.authentication.ResponseHandler
import org.webproject.sso.util.authentication.getTokenFromHeader

@RestController
@RequestMapping("/v1")
class UserController @Autowired constructor(private val userService: UserService) {

    @Autowired lateinit var exchange : KeyExchange

    @PostMapping("/exchange")
    fun exchange(@RequestBody otherSidePublicKey: Int): Int {
        val shared = exchange.calculateShareSecret(otherSidePublicKey)

        return exchange.getShareSecret()

    }


    @PostMapping("/registerUser")
    @FreeAuthentication
    fun registerUser(@Valid @RequestBody user: UserDto,requestServlet: HttpServletRequest): ResponseEntity<Any?> {
        user.deviceModel = requestServlet.getHeader(DEVICE_MODEL_HEADER)
        val tokenRikPair = userService.registerUser(user)
        return ResponseHandler.generateResponse("Ok", HttpStatus.OK, RegisterUserResponse(tokenRikPair.first,tokenRikPair.second))
    }

    @PostMapping("/loginUser")
    @FreeAuthentication
    fun login(@Valid @RequestBody user: UserDto,requestServlet: HttpServletRequest): ResponseEntity<Any?> {
        user.deviceModel = requestServlet.getHeader(DEVICE_MODEL_HEADER)
        val tokenRikPair = userService.login(user)
        return ResponseHandler.generateResponse("Ok", HttpStatus.OK, RegisterUserResponse(tokenRikPair.first,tokenRikPair.second))
    }

    @PostMapping("/sendOtp")
    fun sendOtp(requestServlet: HttpServletRequest): ResponseEntity<Any?> {
        val token = requestServlet.getTokenFromHeader()
        val deviceModel= requestServlet.getHeader(DEVICE_MODEL_HEADER) ?: throw Exception("You did not provided device model")
        val newToken = userService.sendOtp(token.sessionId,deviceModel)
        return ResponseHandler.generateResponse(
            "Ok",
            HttpStatus.OK,
            RegisterUserResponse(newToken,null)
        )
    }

    @PostMapping("/verifyOtp")
    fun verifyOtp(@RequestBody requestModel: OtpRequestModel,requestServlet: HttpServletRequest): ResponseEntity<Any?> {
        val token = requestServlet.getTokenFromHeader()
        val newToken = userService.verifyOtp(token,requestModel)
        return ResponseHandler.generateResponse("Ok",HttpStatus.OK,RegisterUserResponse(verifyToken = newToken,null))
    }

    @PostMapping("/verifyAuth")
    fun verifyAuth(requestServlet: HttpServletRequest): ResponseEntity<Any?>{
        val token = requestServlet.getTokenFromHeader()
        val systemToken = userService.verifyAuth(token)
        return ResponseHandler.generateResponse("Ok",HttpStatus.OK, AuthenticationResponseModel(systemToken))
    }

    @PostMapping("/logout")
    fun logOut(requestServlet: HttpServletRequest): ResponseEntity<Any?> {
        val token = requestServlet.getTokenFromHeader()
        userService.logOut(token)
        return ResponseHandler.generateResponse("Ok",HttpStatus.OK,null)
    }


}