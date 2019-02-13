import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeOutputKind

plugins {
    kotlin("multiplatform")
}

repositories {
    jcenter()
}

kotlin {

    jvm()

    when (System.getenv("SDK_NAME")?.startsWith("iphoneos")) {
        true -> iosArm64("ios")
        else -> iosX64("ios")
    }

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        getByName("jvmMain") {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        getByName("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}

configurations {
    create("compileClasspath")
}