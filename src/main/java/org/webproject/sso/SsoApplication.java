package org.webproject.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


@SpringBootApplication
public class SsoApplication {

    public static void main(String[] args)  {

        //TODO TEST for dockerized
        try {
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5433/", "postgres", "amir4181379");
            Statement statement = c.createStatement();

            statement.executeUpdate("CREATE DATABASE \"ssoDb\"\n" +
                    "    WITH\n" +
                    "    OWNER = postgres\n" +
                    "    ENCODING = 'UTF8'\n" +
                    "    LOCALE_PROVIDER = 'libc'\n" +
                    "    CONNECTION LIMIT = -1\n" +
                    "    IS_TEMPLATE = False;");
        }catch (SQLException e) {

        }

        SpringApplication.run(SsoApplication.class, args);


    }
}
