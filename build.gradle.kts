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
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.DokkaTask
/**
 * The build gradle file for the RadSim Translation Model.
 *
 * @author Armin Schnabel
 */
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.dokka:dokka-base:1.9.0")
    }
}

plugins {
    id("idea")
    // Plugin to display the Gradle task graph
    id("org.barfuin.gradle.taskinfo").version("2.1.0")

    id("maven-publish")
    kotlin("jvm").version("2.1.20")

    id("io.gitlab.arturbosch.detekt").version("1.23.1")
    id("org.jetbrains.dokka").version("1.9.10")
}

group = "de.radsim"
version = "0.0.0" // Automatically overwritten by CI

// --- Dependency Versions ---

extra["gradleWrapperVersion"] = "7.6.1"
extra["cyfaceSerializationVersion"] = "4.1.11"
extra["logbackVersion"] = "1.5.13"

// Versions of testing dependencies
extra["junitVersion"] = "5.9.2"
extra["mockitoKotlinVersion"] = "5.1.0"
extra["hamcrestVersion"] = "2.2"

// Versions of Code management dependencies
extra["dokkaVersion"] = "1.9.10"
extra["detektVersion"] = "1.23.1"


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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22")

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
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.22")

    // Required to create inline documentation
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:${project.extra["dokkaVersion"]}")
    dokkaHtmlPlugin("org.jetbrains.dokka:dokka-base:${project.extra["dokkaVersion"]}")

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
            url = uri("file://${rootProject.buildDir}/repo")
        }
    }
}

// The following repositories are used to load artifacts from.
repositories {
    maven {
        name = "local"
        url = uri("file://${rootProject.buildDir}/repo")
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

tasks.withType<DokkaTask>().configureEach {
    outputDirectory.set(buildDir.resolve("doc/"))
    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        footerMessage = "(c) 2023-2024 Cyface gmbH"
    }
    // TODO: This is not working
    /*dokkaSourceSets {
        named("main") {
            includes.from("README.md")
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(URL("https://github.com/radsim/backend/build/dokka/"))
            }
        }
    }*/
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
