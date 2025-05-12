plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.10.6"
}

group = "com.lexicubes"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

graalvmNative {
    binaries {
        named("main") {
            imageName.set("backend")
            buildArgs.add("--no-fallback")
            buildArgs.add(
                "--initialize-at-build-time=" +
                        "org.apache.commons.logging.LogFactory," +
                        "org.apache.commons.logging.LogFactoryService"
            )
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation(platform("com.google.cloud:spring-cloud-gcp-dependencies:6.1.1"))
    implementation("com.google.cloud:spring-cloud-gcp-starter-secretmanager")
    implementation("com.google.api-client:google-api-client")
    implementation("org.liquibase:liquibase-core")
    implementation("org.jetbrains:annotations:26.0.2")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile>() {
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
