buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(libs.google.services)
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {

    id("com.android.application") version "8.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
//    id("com.chaquo.python") version "15.0.1" apply false
    alias(libs.plugins.googleGmsGoogleServices) apply false
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
}

