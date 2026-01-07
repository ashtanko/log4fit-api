import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.ksp)
}

group = "dev.shtanko"
version = "0.0.1"

application {
    mainClass = "dev.shtanko.ApplicationKt"
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.host.common)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.hikari)
    implementation(libs.bcrypt)
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.koin.ktor)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.dotenv)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.rate.limiting)
    implementation(libs.kotlin.asyncapi.ktor)
    implementation(libs.ktor.server.metrics)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.http.redirect)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.simple.cache)

    implementation(libs.ktor.server.task.scheduling.core)
    implementation(libs.ktor.server.task.scheduling.jdbc)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.mockk)
}

sourceSets {
    val main by getting
    val test by getting

    val integrationTest by creating {
        kotlin.srcDir("src/integrationTest/kotlin")
        resources.srcDir("src/integrationTest/resources")

        compileClasspath += main.output + test.output
        runtimeClasspath += main.output + test.output
    }
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())

val integrationTest by tasks.registering(Test::class) {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    shouldRunAfter("test")

    useJUnitPlatform()
}

tasks.check {
    dependsOn(integrationTest)
}

tasks.register<ShadowJar>("buildDevFatJar") {
    description = "Builds a fat JAR with a 'debug' classifier."
    group = "build"
    archiveClassifier.set("debug-all")
}
