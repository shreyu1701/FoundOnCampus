import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")            // ✅ Required for kotlinOptions
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services") // Firebase
    id("org.jetbrains.kotlin.kapt")

}
val props = Properties()
val propsFile = rootProject.file("local.properties")
if (propsFile.exists()) {
    props.load(propsFile.inputStream())
}

val emailUser = props["EMAIL_USER"]?.toString() ?: ""
val emailPass = props["EMAIL_PASS"]?.toString() ?: ""

android {
    namespace = "com.project.foundoncampus"
    compileSdk = 36

    packaging {
        resources {
            excludes += setOf(
                "META-INF/NOTICE.md",
                "META-INF/LICENSE.md"
            )
        }
    }

    defaultConfig {
        applicationId = "com.project.foundoncampus"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "EMAIL_USER", "\"$emailUser\"")
        buildConfigField("String", "EMAIL_PASS", "\"$emailPass\"")

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

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11" // Compose Compiler for Kotlin 2.0+
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")

    implementation("io.coil-kt:coil-compose:2.4.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")


    // Jetpack DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    //kapt("androidx.room:room-compiler:2.6.1")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")


    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Gson for JSON
    implementation("com.google.code.gson:gson:2.10.1")

    // JavaMail (Gmail SMTP)
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

    // Firebase (optional)
    implementation("com.google.firebase:firebase-analytics-ktx:21.6.1")
    // Firebase BoM for consistent versions
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))

// Add Firebase modules you plan to use:
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database") // for Realtime DB
// OR
    implementation("com.google.firebase:firebase-firestore") // if you prefer Firestore
    implementation ("com.google.firebase:firebase-firestore-ktx")

    implementation("androidx.compose.material3:material3:1.2.0")




    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
}