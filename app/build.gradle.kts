plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
//    id("com.chaquo.python")
    alias(libs.plugins.googleGmsGoogleServices)
    kotlin("kapt")
}

android {

    namespace = "com.example.inzynierkapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.inzynierkapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
            ndk {
                // On Apple silicon, you can omit x86_64.
                abiFilters += listOf("arm64-v8a", "x86_64", "armeabi-v7a", "arm64-v8a", "x86")
            }
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
//        jvmTarget = "1.8"
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
//    flavorDimensions += "pyVersion"
//    productFlavors {
//        create("py38") { dimension = "pyVersion" }
//    }
}
//chaquopy {
//    defaultConfig {
//        version = "3.8"
//        pip {
//            install("torch==1.8.1")
//            install("sentencepiece==0.1.95")
//            install("transformers==4.15.0")
//        }
//    }
//    productFlavors {
//        getByName("py38") { version = "3.8" }
//    }
//    sourceSets {
//        getByName("main") {
//            srcDir("src/main/python")
//        }
//    }
//
//
//}

dependencies {

    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.camera.core)
    implementation("androidx.compose.material:material-icons-extended:1.0.5")
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.runtime.android)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation(libs.androidx.benchmark.macro)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.androidx.appcompat)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:image:4.6.2")

    implementation("androidx.room:room-runtime:2.6.1")
    "kapt"("androidx.room:room-compiler:2.6.1")
//    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1") // Add Room Kotlin extensions
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android") // Add Coroutines

}