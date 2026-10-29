/*
 * Copyright (C) 2019-2025 Cyface GmbH
 *
 * This file is part of the RadSim Translation Model.
 *
 *  The RadSim Translation Model is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  The RadSim Translation Model is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with the RadSim Translation Model.  If not, see <http://www.gnu.org/licenses/>.
 */
import io.gitlab.arturbosch.detekt.Detekt
/**
 * The build gradle file for the RadSim Translation Model.
 *
 * @author Armin Schnabel
 */
plugins {
    id("idea")

    id("maven-publish")
    kotlin("jvm").version("2.4.21")

    id("io.gitlab.arturbosch.detekt").version("1.23.8")
}

group = "de.radsim"
version = "0.0.0" // Automatically overwritten by CI

// --- Dependency Versions ---

extra["gradleWrapperVersion"] = "9.8.2"
extra["kotlinVersion"] = "2.4.21"
extra["cyfaceSerializationVersion"] = "6.1.0"
extra["logbackVersion"] = "1.6.6"

// Versions of testing dependencies
extra["junitVersion"] = "6.1.3"
extra["mockitoKotlinVersion"] = "6.4.0"
extra["hamcrestVersion"] = "3.0"

// Versions of Code management dependencies
extra["detektVersion"] = "1.23.8"


// --- plugin configurations

kotlin {
    jvmToolchain(17)
}

// --- task configurations --

tasks.wrapper {
    gradleVersion = project.extra["gradleWrapperVersion"].toString()
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")

        // Also show assert message (e.g. on the CI) when tests fail to identify cause
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
        showStandardStreams = false
    }
}

dependencies {
    // Kotlin Support
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${project.extra["kotlinVersion"]}")

    // Cyface Dependencies
    implementation("de.cyface:model:${project.extra["cyfaceSerializationVersion"]}") // OsmTag class, mm-output

    // Logging
    implementation("ch.qos.logback:logback-classic:${project.extra["logbackVersion"]}")
    implementation("ch.qos.logback:logback-core:${project.extra["logbackVersion"]}")

    // Testing Dependencies
    testImplementation(platform("org.junit:junit-bom:${project.extra["junitVersion"]}"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params") // Required for parameterized tests
    testImplementation("org.hamcrest:hamcrest:${project.extra["hamcrestVersion"]}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${project.extra["mockitoKotlinVersion"]}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    // Gradle 9 no longer adds the launcher automatically
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.jetbrains.kotlin:kotlin-test:${project.extra["kotlinVersion"]}")

    // Required for Linting - Enables the corresponding sections in config/detekt.yml
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${project.extra["detektVersion"]}")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-rules-libraries:${project.extra["detektVersion"]}")
}

// Maven-publish configuration
publishing {
    publications {
        create<MavenPublication>("translation-model") {
            from(components["java"])
        }
    }

    // The following repositories are used to publish artifacts to.
    repositories {
        maven {
            name = "github"
            url = uri("https://maven.pkg.github.com/radsim/translation-model")
            credentials {
                username = (project.findProperty("gpr.user") ?: System.getenv("USERNAME")) as String
                password = (project.findProperty("gpr.key") ?: System.getenv("PASSWORD")) as String
            }
        }
        maven {
            name = "local"
            url = uri(rootProject.layout.buildDirectory.dir("repo"))
        }
    }
}

// The following repositories are used to load artifacts from.
repositories {
    maven {
        name = "local"
        url = uri(rootProject.layout.buildDirectory.dir("repo"))
    }
    mavenCentral()
    maven {
        name = "github-serializer"
        url = uri("https://maven.pkg.github.com/cyface-de/serializer")
        credentials {
            username = (project.findProperty("gpr.user") ?: System.getenv("USERNAME")) as String?
            password = (project.findProperty("gpr.key") ?: System.getenv("PASSWORD")) as String?
        }
    }
}

// Begin detekt configuration
detekt {
    buildUponDefaultConfig = true // preconfigure defaults
    allRules = false // activate all available (even unstable) rules.
    config.from(files("$rootDir/config/detekt.yml")) // point to custom config, overwriting default behavior
    baseline = file("$projectDir/config/baseline.xml") // a way of suppressing issues before introducing detekt
}

tasks.withType<Detekt>().configureEach {
    reports {
        // observe findings in your browser with structure and code snippets
        html.required.set(true)
        // checkstyle like format mainly for integrations like Jenkins
        // xml.required.set(true)
        // similar to the console output, contains issue signature to manually edit baseline files
        // txt.required.set(true)
        // SARIF format (https://sarifweb.azurewebsites.net/) integrate with Github Code Scan
        // sarif.required.set(true)
        // simple Markdown format
        // md.required.set(true)
    }
}
// End detekt configuration

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")

        // Also show assert message (e.g. on the CI) when tests fail to identify cause
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true // not working with vertx fail() so we disable this
        showStackTraces = true
        showStandardStreams = false
    }
}
