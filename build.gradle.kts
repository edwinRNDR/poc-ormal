import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    maven
    java
    kotlin("jvm") version("1.3.50") apply false
}

buildscript {
    val openrndrUseSnapshot = false

    val mxnetVersion by rootProject.extra("1.5.1-1.5.2")
    val mxnetGPUVersion by rootProject.extra("1.5.1-1.5.2")
    val mklDnnVersion by rootProject.extra("0.21-2-1.5.2")

    val openrndrVersion by rootProject.extra(if (openrndrUseSnapshot) "0.4.0-SNAPSHOT" else "0.3.37")
    val openblasVersion by rootProject.extra("0.3.7-1.5.2")
    val opencvVersion by rootProject.extra("4.1.2-1.5.2")
}

val kotlinVersion = "1.3.50"


val applicationFullLogging = false


// supported features are: video, panel
val openrndrFeatures = setOf("video", "panel")


// supported features are: orx-camera, orx-compositor,orx-easing, orx-filter-extension,orx-file-watcher, orx-kinect-v1
// orx-integral-image, orx-interval-tree, orx-jumpflood,orx-kdtree, orx-mesh-generators,orx-midi, orx-no-clear,
// orx-noise, orx-obj, orx-olive


fun DependencyHandler.openrndr(module: String): Any {
    return "org.openrndr:openrndr-$module:${extra.get("openrndrVersion")}"
}


allprojects {

    group = "org.openrndr.ormal"
    version = "0.3.0-SNAPSHOT"


    apply(plugin = "kotlin")
    apply(plugin = "maven")

    repositories {
        mavenCentral()
        //if (openrndrUseSnapshot) {
            mavenLocal()
        //}
        maven(url = "https://dl.bintray.com/openrndr/openrndr")

        maven(url= "https://oss.sonatype.org/content/repositories/snapshots")


    }

    dependencies {
        implementation(openrndr("core"))
        implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.0-RC")
        implementation("io.github.microutils", "kotlin-logging", "1.7.2")
        implementation(kotlin("stdlib-jdk8"))
        testCompile("junit", "junit", "4.12")
    }
}