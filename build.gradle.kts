import com.adarshr.gradle.testlogger.theme.ThemeType
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.diffplug.gradle.spotless") version "3.23.1"
    id("org.jlleitschuh.gradle.ktlint") version "7.3.0"
    id("io.gitlab.arturbosch.detekt") version "1.0.0-RC16"
    id("org.jetbrains.dokka") version "0.9.18"
    id("com.adarshr.test-logger") version "1.6.0"
    id("edu.wpi.first.GradleJni") version "0.9.0"
    id("com.jfrog.bintray") version "1.8.3"
    java
    cpp
    `maven-publish`
    `java-library`
}

apply {
    plugin("kotlin")
}

val spotlessLicenseHeaderDelimiter = "(@|package|import)"

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(
            group = "org.apache.commons",
            name = "commons-lang3",
            version = property("commons-lang3.version") as String
        )
    }
}

val projectName = "bowler-kinematics-native"
val osName = System.getenv("BOWLER_KINEMATICS_NATIVE_OS_NAME") ?: "UNKNOWN-OS"
val publicationName = "publication-$projectName-${name.toLowerCase()}"
val publishedVersionName = "${property("bowler-kinematics-native.version") as String}-$osName"

group = "com.neuronrobotics"
version = publishedVersionName

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8", property("kotlin.version") as String))
    implementation(
        group = "org.apache.commons",
        name = "commons-lang3",
        version = property("commons-lang3.version") as String
    )

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.1.0")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.1.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=enable", "-progressive")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STARTED
        )
        displayGranularity = 0
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = TestExceptionFormat.FULL
    }
}

testlogger {
    theme = ThemeType.STANDARD_PARALLEL
}

apply {
    from(rootProject.file("gradle/jniBuild.gradle"))
}

spotless {
    /*
     * We use spotless to lint the Gradle Kotlin DSL files that make up the build.
     * These checks are dependencies of the `check` task.
     */
    kotlinGradle {
        ktlint(property("ktlint.version") as String)
        trimTrailingWhitespace()
    }

    format("extraneous") {
        target("src/**/*.fxml")
        trimTrailingWhitespace()
        indentWithSpaces(2)
        endWithNewline()
    }

    java {
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        indentWithSpaces(2)
        endWithNewline()
        @Suppress("INACCESSIBLE_TYPE")
        licenseHeaderFile(
            "${rootProject.rootDir}/config/spotless/bowler-kinematics-native.license",
            spotlessLicenseHeaderDelimiter
        )
    }

    kotlin {
        ktlint(property("ktlint.version") as String)
        trimTrailingWhitespace()
        indentWithSpaces(2)
        endWithNewline()
        @Suppress("INACCESSIBLE_TYPE")
        licenseHeaderFile(
            "${rootProject.rootDir}/config/spotless/bowler-kinematics-native.license",
            spotlessLicenseHeaderDelimiter
        )
    }
}

val jar by tasks.existing(Jar::class) {
    dependsOn(":bowler_kinematics_native_native_librarySharedLibrary")
    from({ File("$buildDir/libs/bowler_kinematics_native_native_library/shared/").listFiles()!! })
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    archiveBaseName.set(projectName)
    from(sourceSets.main.get().allSource)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    archiveBaseName.set(projectName)
    from(tasks.dokka)
}

if (osName != "UNKNOWN-OS") {
    publishing {
        publications {
            create<MavenPublication>(publicationName) {
                artifactId = projectName
                from(components["java"])
                artifact(sourcesJar)
                artifact(dokkaJar)
            }
        }
    }

    bintray {
        val bintrayApiUser = properties["bintray.api.user"] ?: System.getenv("BINTRAY_USER")
        val bintrayApiKey = properties["bintray.api.key"] ?: System.getenv("BINTRAY_API_KEY")
        user = bintrayApiUser as String?
        key = bintrayApiKey as String?
        setPublications(publicationName)
        with(pkg) {
            repo = "maven-artifacts"
            name = projectName
            userOrg = "commonwealthrobotics"
            publish = true
            setLicenses("LGPL-3.0")
            vcsUrl = "https://github.com/CommonWealthRobotics/bowler-kinematics-native.git"
            githubRepo = "https://github.com/CommonWealthRobotics/bowler-kinematics-native"
            with(version) {
                name = publishedVersionName
                desc = "bowler-kinematics implemented natively."
            }
        }
    }
}

tasks.dokka {
    dependsOn(tasks.classes)
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}

tasks.wrapper {
    gradleVersion = rootProject.property("gradle-wrapper.version") as String
    distributionType = Wrapper.DistributionType.ALL
}
