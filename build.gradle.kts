import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION") plugins {
    application
    alias(libs.plugins.kotest.multiplatform)
    id(libs.plugins.kotlin.jvm.pluginId)
    alias(libs.plugins.dokka)
    id(libs.plugins.detekt.pluginId)
    alias(libs.plugins.kover)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqldelight)
}

application {
    mainClass by "io.github.qohat.MainKt"
}

sqldelight {
    database("SqlDelight") {
        packageName = "io.github.qohat.sqldelight"
        dialect(libs.sqldelight.postgresql.get())
    }
}

allprojects {
    extra.set("dokka.outputDirectory", rootDir.resolve("docs"))
    setupDetekt()
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "${JavaVersion.VERSION_11}"
            freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
        }
    }

    test {
        useJUnitPlatform()
        extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
            includes = listOf("io.github.qohat.*")
        }
    }
}

dependencies {
    implementation(libs.arrow.core)
    implementation(libs.arrow.fx)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.defaultheaders)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.auth)
    implementation(libs.logback.classic)
    implementation(libs.sqldelight.jdbc)
    implementation(libs.hikari)
    implementation(libs.postgresql)
    implementation(libs.kotlin.kafka)

    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.ktor.client.serialization)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotest.arrow)
    testImplementation(libs.kotest.runnerJUnit5)
    testImplementation(libs.kotest.frameworkEngine)
    testImplementation(libs.kotest.assertionsCore)
    testImplementation(libs.kotest.property)
}