import org.apache.commons.lang3.SystemUtils
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    cpp
    id("edu.wpi.first.GradleJni") version "0.9.0"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.3"
    `java-library`
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath(group = "org.apache.commons", name = "commons-lang3", version = property("commons-lang3.version") as String)
    }
}

val projectName = "bowler-kinematics-native"
group = "com.neuronrobotics"
version = property("bowler-kinematics-native.version") as String

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(group = "org.apache.commons", name = "commons-lang3", version = property("commons-lang3.version") as String)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.1.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.1.0")
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

apply {
    from(rootProject.file("gradle/jniBuild.gradle"))
}

val jar by tasks.existing(Jar::class) {
    dependsOn(":bowler_kinematics_native_native_librarySharedLibrary")
    from({ File("$buildDir/libs/bowler_kinematics_native_native_library/shared/").listFiles()!! })
}

val publicationName = "publication-$projectName-${name.toLowerCase()}"
val artifactName = "$projectName-${SystemUtils.OS_NAME.toLowerCase().replace(" ", "")}"

publishing {
    publications {
        create<MavenPublication>(publicationName) {
            artifactId = artifactName
            from(components["java"])
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
            name = property("bowler-kinematics-native.version") as String
            desc = "bowler-kinematics implemented natively."
        }
    }
}
