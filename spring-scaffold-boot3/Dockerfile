# 로컬 레벨에서 Jar빌드 후 Jar파일만 복제하여 이미지를 빌드한다.
FROM openjdk:21

ARG JAR_FILE=./build/libs/spring-scaffold-boot3-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /usr/src/Backend/app.jar
WORKDIR /usr/src/Backend

ENTRYPOINT ["java", "-jar", "./app.jar"]

EXPOSE 8080
