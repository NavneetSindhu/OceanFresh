// app/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.oceanfresh"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.oceanfresh"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM — keeps all Compose versions in sync
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    val room_version = "2.6.1" // Current stable version for 2026

    // Room components
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    // Use ksp if you have the KSP plugin, otherwise use kapt
    ksp("androidx.room:room-compiler:$room_version")

    // Gson for JSON serialization (used in TypeConverters)
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("androidx.compose.material:material-icons-extended") // extra icons

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    implementation("androidx.datastore:datastore-preferences:1.1.1")
    // Coil — async image loading (for product images from URL / drawable)
    implementation("io.coil-kt:coil-compose:2.6.0")

    debugImplementation(libs.androidx.ui.tooling)
}
