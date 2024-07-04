package org.webproject.sso.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.webproject.responsewrapper.custom.exception.*
import org.webproject.sso.authentication.CryptoHelper
import org.webproject.sso.authentication.KeyExchange
import org.webproject.sso.authentication.TokenManagement
import org.webproject.sso.authentication.TokenType
import org.webproject.sso.model.dao.user.Session
import org.webproject.sso.model.dao.user.User
import org.webproject.sso.model.dto.UserDto
import org.webproject.sso.model.dto.otp.OtpRequestModel
import org.webproject.sso.repository.SessionRepository
import org.webproject.sso.repository.UserRepository
import java.util.*

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository,
    private val keyExchange: KeyExchange,
    private val emailService: EmailService
)
{

    @Transactional
     fun registerUser(userDto: UserDto): Pair<String,Int> {
         //TODO enable hell-man Key Exchange
        if(userDto.userPublicKey==0 || userDto.deviceModel == null) throw SupportedMissMatchException("One or Some of Required parameter is not passed")

         val userDb= userRepository.findUserByEmail(userDto.email)

         if(userDb != null && !userDb.sessionList.isNullOrEmpty()) throw SupportedReplayedException("user is already registered")

         val itUser=  User().apply {
             this.email = userDto.email
             hashedPass = CryptoHelper.hashPassword(userDto.password)
             firstName = userDto.firstName
             lastName = userDto.lastName
             type = userDto.getUserType()
         }

        itUser.sessionList.add(
            Session(userDto.deviceModel,keyExchange.getSecretKey(),userDto.userPublicKey).apply {
                user = itUser
            }
        )
         userRepository.save(itUser)

         val verifyToken = TokenManagement.generateToken(itUser.id, TokenType.VERIFY,userDto.deviceModel!!)

         return verifyToken to keyExchange.getShareSecret()
    }

    fun login(userDto: UserDto): Pair<String,Int> {
        //TODO enable hell-man Key Exchange
        if(userDto.userPublicKey==0 || userDto.deviceModel == null) throw SupportedMissMatchException("One or Some of Required parameter is not passed")

        val userDb= userRepository.findUserByEmail(userDto.email)

        if(userDb == null || userDb.hashedPass != CryptoHelper.hashPassword(userDto.password)) throw DefaultSupportedException("Your email or password is incorrect")

        if(userDb.token_count == -1) throw DefaultSupportedException("Your must first complete registration first!")

        var itSession = userDb.sessionList.find { it.deviceOwner == userDto.deviceModel }

        if(itSession != null) {
            userDb.token_count--
            itSession.isOtpVerified = false
        }else {
            itSession = Session(
                userDto.deviceModel,keyExchange.getSecretKey(),userDto.userPublicKey
            ).apply {
                user = userDb
            }
        }
        userDb.sessionList.add(itSession)

        userRepository.save(userDb)

        val verifyToken = TokenManagement.generateToken(userDb.id, TokenType.VERIFY,userDto.deviceModel!!)

        return verifyToken to keyExchange.getShareSecret()
    }

    fun sendOtp(userId: UUID,deviceModel: String): String {

        val userDb = userRepository.findById(userId).orElseThrow { DefaultSupportedException("Your User id is not Valid!") }

        val itSession = userDb.sessionList.find { it.deviceOwner == deviceModel } ?: throw DefaultSupportedException("Your Header Content is not Valid")

        if(itSession.isOtpVerified) throw DefaultSupportedException("You have Already Verified Your Otp Authentication")

        val tmpCode = Random().nextInt(1000, 10000)
        emailService.sendEmail(userDb.email,"Otp Verification","Code is: $tmpCode")
        return TokenManagement.generateToken(userId, TokenType.VERIFY,deviceModel, tmpCode)

    }

    fun verifyOtp(token: TokenManagement.Token, otpModel: OtpRequestModel): String{
        val userDb= userRepository.findById(token.sessionId).orElseThrow { DefaultSupportedException("Your User id is not Valid!") }

        val itSession = userDb.sessionList.find { it.deviceOwner == token.deviceModel } ?: throw Exception("Your Header Content is not Valid")

        if(itSession.isOtpVerified) throw DefaultSupportedException("This user otp verification has done before!")

        val actualCode=(token.payload as? Double)?.toInt() ?: throw SupportedMissMatchException("Your Token is not Valid!")

        if(actualCode != otpModel.otp) throw DefaultSupportedException("Invalid OTP")

        itSession.isOtpVerified = true

        userRepository.save(userDb)
        return TokenManagement.generateToken(token.sessionId,TokenType.VERIFY,token.deviceModel)
    }

    fun verifyAuth(token: TokenManagement.Token): String {
        val userDb= userRepository.findById(token.sessionId).orElseThrow { DefaultSupportedException("Your User id is not Valid!") }

        val itSession = userDb.sessionList.find { it.deviceOwner == token.deviceModel } ?: throw DefaultSupportedException("Your Header Content is not Valid")

        if(!itSession.isOtpVerified) throw DefaultSupportedException("Verification process of this user is not done yet!")

        if(userDb.token_count == -1) {
            //Register process
            userDb.token_count = 1
        }else {
            //Login process
            userDb.token_count++
        }
        userRepository.save(userDb)

        return TokenManagement.generateToken(token.sessionId,TokenType.SYSTEM,itSession.deviceOwner,itSession.sessionId)

    }


    fun logOut(token: TokenManagement.Token) {

        val userDb = userRepository.findById(token.sessionId).orElseThrow { DefaultSupportedException("Your User id is not Valid!") }

        val itSession = userDb.sessionList.find { it.sessionId == token.payload } ?: throw DefaultSupportedException("You Are Already Log out")

        sessionRepository.delete(itSession)
        userDb.sessionList.remove(itSession)

        userDb.token_count = userDb.sessionList.size

        userRepository.save(userDb)

    }

}