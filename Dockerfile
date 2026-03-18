# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace/app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

RUN java -Djarmode=tools -jar build/libs/*.jar extract --layers --destination extracted

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /workspace/app/extracted/dependencies/ ./
COPY --from=builder /workspace/app/extracted/spring-boot-loader/ ./
COPY --from=builder /workspace/app/extracted/snapshot-dependencies/ ./
COPY --from=builder /workspace/app/extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]