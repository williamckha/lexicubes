FROM gradle:jdk21-alpine AS build
WORKDIR /app

COPY --chown=gradle:gradle build.gradle* settings.gradle* ./
COPY --chown=gradle:gradle gradle ./gradle
COPY --chown=gradle:gradle src ./src

RUN gradle dependencies --no-daemon
RUN gradle bootJar --no-daemon

# Unpack the jar file
RUN mkdir -p build/dependency && (cd build/dependency; echo $(ls -1 ../libs); jar -xf ../libs/*.jar)

FROM gcr.io/distroless/java21-debian12
WORKDIR /app
USER nonroot

ARG DEPENDENCY=/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

EXPOSE 8080

ENTRYPOINT [                                    \
    "java", "-cp", "/app:/app/lib/*",           \
    "-Dspring.profiles.active=prod",            \
    "com.lexicubes.backend.BackendApplication"  \
]