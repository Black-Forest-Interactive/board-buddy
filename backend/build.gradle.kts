import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.allopen") version "2.2.21"
    kotlin("plugin.jpa") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("com.google.devtools.ksp") version "2.3.0"
    id("org.sonarqube") version "7.0.1.6134"
    id("net.researchgate.release") version "3.1.0"
    id("com.google.cloud.tools.jib") version "3.4.5"
    id("io.micronaut.application") version "4.6.1"
    id("io.micronaut.test-resources") version "4.6.1"
    id("io.micronaut.aot") version "4.6.1"
    id("maven-publish")
    id("jacoco")
}


repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        mavenContent { snapshotsOnly() }
    }
    mavenCentral()
    maven("https://maven.tryformation.com/releases") {
        content {
            includeGroup("com.jillesvangurp")
        }
    }
}
dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.20")
    runtimeOnly("org.yaml:snakeyaml")

    testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.1")
    testImplementation("io.mockk:mockk:1.14.6")

    // jackson
    ksp("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut:micronaut-jackson-databind")
//    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // http
    ksp("io.micronaut:micronaut-http-validation")
    compileOnly("io.micronaut:micronaut-http-client")

    // validation
    implementation("jakarta.validation:jakarta.validation-api")
    ksp("io.micronaut.validation:micronaut-validation-processor")
    implementation("io.micronaut.validation:micronaut-validation")

    // openapi
    ksp("io.micronaut.openapi:micronaut-openapi")
    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")
    implementation("io.swagger.core.v3:swagger-annotations")

    // security
    ksp("io.micronaut.security:micronaut-security-annotations")
    implementation("io.micronaut.security:micronaut-security")
    implementation("io.micronaut.security:micronaut-security-jwt")
    implementation("io.micronaut.security:micronaut-security-oauth2")
    aotPlugins("io.micronaut.security:micronaut-security-aot:4.15.0")

    // kotlin
    implementation("io.micronaut.kotlin:micronaut-kotlin-extension-functions")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.21")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.21")

    // caching
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")

    // coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.2")
    // reactor
    implementation("io.micronaut.reactor:micronaut-reactor")
    implementation("io.micronaut.reactor:micronaut-reactor-http-client")
    // data
    ksp("io.micronaut.data:micronaut-data-processor")
    implementation("io.micronaut.data:micronaut-data-jdbc")
    implementation("io.micronaut.flyway:micronaut-flyway")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    runtimeOnly("org.postgresql:postgresql")

    // qrcode
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")

    // mail
    implementation("org.simplejavamail:simple-java-mail:8.12.6")
    implementation("org.simplejavamail:batch-module:8.12.6")
    implementation("org.simplejavamail:authenticated-socks-module:8.12.6")

    // test
    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.opensearch:opensearch-testcontainers:4.0.0")
    testImplementation("io.micronaut.test:micronaut-test-rest-assured")
    testImplementation("io.fusionauth:fusionauth-jwt:5.3.3")
    testImplementation("io.mockk:mockk:1.14.6")

    implementation("jakarta.annotation:jakarta.annotation-api")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")

    // opensearch
    implementation("com.jillesvangurp:search-client:2.6.7")
    // jsoup
    implementation("org.jsoup:jsoup:1.21.2")
}


application {
    mainClass = "de.sambalmueslie.boardbuddy.BoardBuddyApplication"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}


graalvmNative.toolchainDetection = false

micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("de.sambalmueslie.boardbuddy.*")
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}


tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {
    jdkVersion = "25"
}


tasks {
    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    compileTestKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}



tasks.test {
    useJUnitPlatform()
    jvmArgs = listOf(
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseParallelGC"
    )
    finalizedBy(tasks.jacocoTestReport)
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        csv.required.set(false)
    }
}
jacoco {
    toolVersion = "0.8.13"
}

sonar {
    properties {
        property("sonar.projectKey", "Black-Forest-Interactive_board-buddy")
        property("sonar.organization", "black-forest-interactive")
    }
}


jib {
    from.image = "eclipse-temurin:21-jre-ubi9-minimal"
    to {
        image = "open-event-backend"
        tags = setOf(version.toString(), "latest")
    }
    container {
        creationTime.set("USE_CURRENT_TIMESTAMP")

        jvmFlags = listOf(
            "-server",
            "-XX:+UseContainerSupport",
            "-XX:MaxRAMPercentage=75.0",

            // Java 21+ ZGC for better performance
            "-XX:+UseZGC",
            "-XX:+UnlockExperimentalVMOptions",

            "-XX:+TieredCompilation",
            "-Dmicronaut.runtime.environment=prod",
            "-Dio.netty.allocator.maxOrder=3"
        )

        user = "1001"

        environment = mapOf(
            "JAVA_TOOL_OPTIONS" to "-XX:+ExitOnOutOfMemoryError"
        )
    }
}
