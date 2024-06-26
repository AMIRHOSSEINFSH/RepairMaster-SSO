package org.webproject.sso;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.webproject.sso.service.SessionOptService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


@SpringBootApplication
@EnableScheduling
public class SsoApplication {

    public static void main(String[] args)  {


        /*//TODO TEST for dockerized
        try {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "admin", "admin");
            Statement statement = c.createStatement();

            statement.executeUpdate("CREATE DATABASE \"ssoDb\"\n" +
                    "    WITH\n" +
                    "    OWNER = postgres\n" +
                    "    ENCODING = 'UTF8'\n" +
                    "    LOCALE_PROVIDER = 'libc'\n" +
                    "    CONNECTION LIMIT = -1\n" +
                    "    IS_TEMPLATE = False;");
        }catch (SQLException e) {

        }*/

        SpringApplication.run(SsoApplication.class, args);



    }
}
