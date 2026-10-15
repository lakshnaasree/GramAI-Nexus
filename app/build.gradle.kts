import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.gramainexus.smartvillage"
    compileSdk = 37

    // Read Gemini API key from local.properties
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")

    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    }

    val geminiApiKey =
        localProperties.getProperty("GEMINI_API_KEY") ?: ""
    defaultConfig {
        applicationId = "com.gramainexus.smartvillage"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        // Make the API key available through BuildConfig
        buildConfigField(
            "String",
            "GEMINI_API_KEY",
            "\"$geminiApiKey\""
        )

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.media3:media3-exoplayer:1.8.0")
    implementation("androidx.media3:media3-ui:1.8.0")
    // ==========================================
    // Gemini API / Networking
    // ==========================================
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // ==========================================
    // Firebase
    // ==========================================


    implementation("com.google.mlkit:text-recognition:16.0.1")
    implementation("com.google.firebase:firebase-auth")
    implementation(platform("com.google.firebase:firebase-bom:34.2.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // ==========================================
    // Jetpack Compose
    // ==========================================
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // Material Icons
    implementation("androidx.compose.material:material-icons-extended")

    // ==========================================
    // AndroidX
    // ==========================================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ==========================================
    // Navigation
    // ==========================================
    implementation("androidx.navigation:navigation-compose:2.9.4")

    // ==========================================
    // Location
    // ==========================================
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // ==========================================
    // TensorFlow Lite
    // ==========================================
    implementation("org.tensorflow:tensorflow-lite:2.17.0")

    // ==========================================
    // DataStore
    // ==========================================
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // ==========================================
    // Coil 2
    // ==========================================
    implementation("io.coil-kt:coil-compose:2.7.0")

    // ==========================================
    // Coil 3
    // ==========================================
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")

    // ==========================================
    // Biometric
    // ==========================================
    implementation("androidx.biometric:biometric:1.1.0")

    // ==========================================
    // Testing
    // ==========================================
    testImplementation(libs.junit)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}