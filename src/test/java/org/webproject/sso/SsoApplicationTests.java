package org.webproject.sso;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.webproject.sso.service.EmailService;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

@SpringBootTest
class SsoApplicationTests {

    @Autowired
    EmailService emailService;

    @Test
    void sendEMail() {
        String subject = "Email Verfication";
        String body = "Your verification OTP is 1123";
        emailService.sendEmail("amirhosseinfsh2000@gmail.com",subject,body);
    }

    @Test
    void contextLoads() {
        try {
            SecretKey s= generateKey(256);
            Base64.getEncoder().encodeToString(s.getEncoded());
        }catch (Exception e) {
            e.printStackTrace();
        }


    }


    public SecretKey generateKey(int n) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(n);
        return keyGen.generateKey();
    }

}
