FROM openjdk:21

EXPOSE 8081

ADD target/sso-0.0.1-SNAPSHOT.jar app-sso.jar

ENTRYPOINT ["java","-jar","app-sso.jar"]