FROM openjdk:11
ARG JAR_FILE=build/libs/todo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENV TZ Asia/Seoul
ENTRYPOINT ["java","-jar","/app.jar", "--spring.profiles.active=prod"]
