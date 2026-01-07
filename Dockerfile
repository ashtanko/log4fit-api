#FROM gradle:8-jdk17 AS base
#COPY --chown=gradle:gradle . /home/gradle/src
#WORKDIR /home/gradle/src
#RUN gradle dependencies --no-daemon
#
## Development stage
#FROM base AS development
#COPY . .
#EXPOSE 8082
#CMD ["./gradlew", "run"]
#
## Build stage
#FROM base AS build
#COPY . .
#RUN gradle buildFatJar --no-daemon
#
## Production stage
#FROM eclipse-temurin:17-jre AS production
#EXPOSE 8082
#RUN mkdir /app
#COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
#ENTRYPOINT ["java","-jar","/app/app.jar"]

# Base stage
FROM gradle:8-jdk17 AS base
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon

# Development stage
FROM base AS development
COPY . .
EXPOSE 8082
CMD ["./gradlew", "run"]

# Build stage
FROM base AS build
COPY . .
RUN gradle clean && gradle buildFatJar --no-daemon

# Build dev stage
FROM base AS build-dev
COPY . .
RUN gradle buildDevFatJar --no-daemon

# Dev stage
FROM eclipse-temurin:17-jre AS dev
WORKDIR /app
#COPY --from=build-dev /app/build/libs/*-debug-all.jar app.jar
COPY --from=build /app/build/libs/*-all.jar app.jar
EXPOSE 8084
CMD ["java", "-jar", "app.jar"]

# Production stage
FROM eclipse-temurin:17-jre AS production
WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar
EXPOSE 8083
CMD ["java", "-jar", "app.jar"]

