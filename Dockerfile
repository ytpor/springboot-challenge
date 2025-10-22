# Stage 1: Build the application
FROM gradle:8.14.3-jdk21 AS builder
WORKDIR /app
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew dependencies --no-daemon
COPY src ./src
ARG SPRING_PROFILE=dev
RUN ./gradlew clean build -x test --no-daemon -Pprofile=${SPRING_PROFILE}

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-noble
WORKDIR /app
ARG SPRING_PROFILE=dev
RUN groupadd -r spring && \
    useradd -r -g spring spring  && \
    chown spring:spring /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8180
USER spring
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILE}
ENTRYPOINT ["java", "-jar", "app.jar"]
