plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.bristot.cryptoapp.network"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    api(project(":common"))

    implementation(libs.squareup.retrofit.retrofit2)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.squareup.retrofit.converter)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
}
