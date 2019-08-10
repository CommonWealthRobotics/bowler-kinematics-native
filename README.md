# bowler-kinematics-native

[![Join the chat at https://gitter.im/madhephaestus/BowlerStudioDevelopmentDiscussion](https://badges.gitter.im/madhephaestus/BowlerStudioDevelopmentDiscussion.svg)](https://gitter.im/madhephaestus/BowlerStudioDevelopmentDiscussion?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Build Status](https://dev.azure.com/commonwealthrobotics/bowler-kinematics-native/_apis/build/status/CommonWealthRobotics.bowler-kinematics-native?branchName=master)](https://dev.azure.com/commonwealthrobotics/bowler-kinematics-native/_build/latest?definitionId=2&branchName=master)
[![Download](https://api.bintray.com/packages/commonwealthrobotics/maven-artifacts/bowler-kinematics-native/images/download.svg)](https://bintray.com/commonwealthrobotics/maven-artifacts/bowler-kinematics-native)
[![stability-experimental](https://img.shields.io/badge/stability-experimental-orange.svg)](https://github.com/emersion/stability-badges#experimental)
[![License: LGPL-3.0](https://img.shields.io/github/license/CommonWealthRobotics/bowler-kinematics.svg)](https://img.shields.io/github/license/CommonWealthRobotics/bowler-kinematics.svg)

Native kinematics for the Bowler stack.

## Use bowler-kinematics-native

Get the artifacts [here](https://bintray.com/commonwealthrobotics/maven-artifacts/bowler-kinematics-native):
```kotlin
maven("https://dl.bintray.com/commonwealthrobotics/maven-artifacts")
```

Although the latest version number may be `x.x.x-linuxx86-64`, `x.x.x-osxx86-64`, or
`x.x.x-windowsx86-64`, there are multiple variants of the same version available. Declare the
correct dependency for your platform using:

In `gradle.properties`:
```properties
bowler-kinematics-native.partial-version=x.x.x
```

In your build file:
```kotlin
fun desktopArch(): String {
    val arch = System.getProperty("os.arch")
    return if (arch == "amd64" || arch == "x86_64") "x86-64" else "x86"
}

fun bowlerKinematicsNativeVersionSuffix() = when {
    SystemUtils.IS_OS_WINDOWS -> "windows"
    SystemUtils.IS_OS_LINUX -> "linux"
    SystemUtils.IS_OS_MAC -> "macos"
    else -> throw IllegalStateException("Unknown OS: ${SystemUtils.OS_NAME}")
} + desktopArch()

fun DependencyHandler.bowlerKinematicsNative() =
    create(
        group = "com.neuronrobotics",
        name = "bowler-kinematics-native",
        version = property("bowler-kinematics-native.partial-version") as String + "-" +
            bowlerKinematicsNativeVersionSuffix()
    )
   
dependencies {
    implementation(bowlerKinematicsNative())
}
```
