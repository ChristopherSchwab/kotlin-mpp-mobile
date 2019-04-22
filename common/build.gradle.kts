import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeOutputKind

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
}

repositories {
    jcenter()
    maven {
        setUrl("https://kotlin.bintray.com/kotlinx")
        setUrl("https://dl.bintray.com/soywiz/soywiz")
    }
}

kotlin {
    jvm()

    when (System.getenv("SDK_NAME")?.startsWith("iphoneos")) {
        true -> iosArm64("ios")
        else -> iosX64("ios")
    }.apply {
        binaries {
            framework(listOf(DEBUG, RELEASE))
        }
    }

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.ktor:ktor-client-core:1.0.0")
                implementation("io.ktor:ktor-client-json:1.0.0")
                implementation("com.soywiz:klock-metadata:1.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.0.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.10.0")
            }
        }
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.mockk:mockk:1.9.3")
                implementation("io.ktor:ktor-client-mock:1.0.0")
            }
        }
        getByName("jvmMain") {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("io.ktor:ktor-client-core-jvm:1.0.0")
                implementation("io.ktor:ktor-client-json-jvm:1.0.0")
                implementation("com.soywiz:klock-jvm:1.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.0.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.10.0")
            }
        }
        getByName("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("io.ktor:ktor-client-mock-jvm:1.0.0")
            }
        }
        getByName("iosMain") {
            dependencies {
                implementation("io.ktor:ktor-client-core-ios:1.0.0")
                implementation("io.ktor:ktor-client-ios:1.0.0")
                implementation("io.ktor:ktor-client-json-ios:1.0.0")
                implementation("com.soywiz:klock-iosx64:1.4.0")
                implementation("com.soywiz:klock-iosarm32:1.4.0")
                implementation("com.soywiz:klock-iosarm64:1.4.0")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.0.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:0.10.0")
            }
        }
        getByName("iosTest") {
            dependencies {
                implementation("io.ktor:ktor-client-mock-native:1.0.0")
            }
        }
    }
}

configurations {
    create("compileClasspath")
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
fun NamedDomainObjectCollection<KotlinTarget>.findByName(nativeTarget: String) = findByName(nativeTarget) as KotlinNativeTarget

tasks {
    val check by getting

    val iosTest by registering {
        onlyIf { OperatingSystem.current().isMacOsX }

        dependsOn("linkTestDebugExecutableIos")
        group = "verification"

        val device = project.findProject("iosDevice")?.toString() ?: "iPhone 8"

        doLast {
            val binary = kotlin.targets.findByName(nativeTarget = "ios").compilations["test"].getBinary(NativeOutputKind.EXECUTABLE, NativeBuildType.DEBUG)
            exec {
                commandLine("xcrun", "simctl", "spawn", device, binary.absolutePath)
            }
        }
    }

    check.dependsOn(iosTest)

    /**
     * This task attaches a native framework built from the ios module to the Xcode project.
     * This task should not be run directly as it requires certain properties passed when running it.
     * Xcode runs this task itself during its build process.
     */
    val copyFramework by registering {
        val buildType: String = project.findProperty("kotlin.build.type")?.toString() ?: "DEBUG"
        val target = project.findProperty("kotlin.target")?.toString() ?: "ios"
        dependsOn("link${buildType.toLowerCase().capitalize()}Framework${target.capitalize()}")

        doLast {
            val srcFile = kotlin.targets.findByName(nativeTarget = target).binaries.findFramework(buildType)!!.outputFile
            val targetDir = project.findProperty("configuration.build.dir")!!
            copy {
                from(srcFile.parent)
                into(targetDir)
                include("common.framework/**")
                include("common.framework.dSYM")
            }
        }
    }
}