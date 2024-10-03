FROM gradle:8-jdk21-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle user:bootJar

FROM openjdk:22-slim
EXPOSE 8080

RUN mkdir /app

COPY --from=build /home/gradle/src/user/build/libs/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
