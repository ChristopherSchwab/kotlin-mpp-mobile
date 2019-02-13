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

val NamedDomainObjectCollection<KotlinTarget>.ios: KotlinNativeTarget
    get() = findByName("ios") as KotlinNativeTarget

tasks {
    val check by getting

    val iosTest by registering {
        onlyIf { OperatingSystem.current().isMacOsX }

        dependsOn("linkTestDebugExecutableIos")
        group = "verification"

        val device = project.findProject("iosDevice")?.toString() ?: "iPhone 8"

        doLast {
            val binary = kotlin.targets.ios.compilations["test"].getBinary(NativeOutputKind.EXECUTABLE, NativeBuildType.DEBUG)
            exec {
                commandLine("xcrun", "simctl", "spawn", device, binary.absolutePath)
            }
        }
    }

    check.dependsOn(iosTest)
}