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
    }.apply {
        binaries {
            framework(listOf(DEBUG, RELEASE))
        }
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