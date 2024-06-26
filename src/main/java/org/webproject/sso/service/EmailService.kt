package org.webproject.sso.service

import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import java.security.SecureRandom
import kotlin.random.Random


@Service
class EmailService constructor(@Autowired private val mailSender: JavaMailSender) {


    fun sendEmail(toEmail: String, subject: String,body: String) {
        val message: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message,true)
//        helper.setFrom("contact@gmail.com", "Repair System Support")
        helper.setTo(toEmail)
        helper.setSubject(subject)
        helper.setText(body,true)
        mailSender.send(message)
        println("code is:$body")
        println("Email was sent")
    }

}