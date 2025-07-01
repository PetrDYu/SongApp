plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "2.1.20"
    id("com.google.devtools.ksp") version "2.1.20-2.0.1"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
}

android {
    namespace = "ru.petr.songapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.petr.songapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 6
        versionName = "1.1.0_beta2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    applicationVariants.all {
        outputs.all {
            this as com.android.build.gradle.internal.api.ApkVariantOutputImpl
            val apkName = "${rootProject.name}_${defaultConfig.versionName}_${buildType.name}.apk"

            outputFileName = apkName
        }
    }
}



dependencies {

    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.20")
    implementation("com.google.devtools.ksp:symbol-processing-api:2.1.20-2.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation(platform("androidx.compose:compose-bom:2025.06.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation("com.arkivanov.decompose:decompose:3.3.0")
    implementation("com.arkivanov.decompose:extensions-compose:3.3.0")
    implementation("com.arkivanov.essenty:lifecycle-coroutines:2.5.0")
    implementation("androidx.room:room-ktx:2.7.2")
    // Jetpack Datastore
    implementation("androidx.datastore:datastore-preferences:1.1.7")
    implementation("androidx.datastore:datastore-core:1.1.7")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.8.3")

    ksp("androidx.room:room-compiler:2.7.2")
    androidTestImplementation("androidx.room:room-testing:2.7.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    implementation("com.github.skydoves:balloon-compose:1.6.12")

    // Constraint layout compose
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.06.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}