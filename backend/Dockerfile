FROM ghcr.io/graalvm/native-image-community:21 AS build
WORKDIR /app

RUN microdnf install findutils

COPY build.gradle* settings.gradle* gradlew ./
COPY gradle ./gradle
COPY src ./src

RUN chmod +x ./gradlew
RUN ./gradlew nativeCompile

FROM oraclelinux:9-slim
WORKDIR /app

COPY --from=build /app/build/native/nativeCompile/backend /app/application

EXPOSE 8080

ENTRYPOINT ["/app/application", "-Dspring.profiles.active=prod"]