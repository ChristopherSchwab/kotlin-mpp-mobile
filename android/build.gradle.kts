plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

repositories {
    google()
    jcenter()
    maven {
        setUrl("https://kotlin.bintray.com/kotlinx")
        setUrl("https://dl.bintray.com/soywiz/soywiz")
    }
    mavenCentral()
}

android {
    compileSdkVersion(28)

    sourceSets {
        getByName("main").java.srcDirs("src/main/kotlin")
        getByName("test").java.srcDirs("src/test/kotlin")
    }

    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 1

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    packagingOptions {
        pickFirst("META-INF/klock.kotlin_module")
    }
}

dependencies {
    implementation(project(":common"))
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("com.android.support.constraint:constraint-layout:1.1.3")
    implementation("com.android.support:design:28.0.0")
    implementation("com.android.support:recyclerview-v7:28.0.0")
    implementation("com.android.support:cardview-v7:28.0.0")
    implementation("io.ktor:ktor-client-json-jvm:1.0.0")
    implementation("io.ktor:ktor-client-okhttp:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.0.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
}