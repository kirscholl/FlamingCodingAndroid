plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.flamingcoding"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.flamingcoding"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    val buildMode = project.findProperty("buildMode")
    val isRelease = buildMode == "release"
    if (isRelease) {
        implementation(project(":trials"))
    }

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("com.google.android.material:material:1.13.0")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")

    // ########################################### Room ############################################
    val room_version = "2.8.4"
    implementation("androidx.room:room-runtime:$room_version")
    ksp("androidx.room:room-compiler:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-rxjava2:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")
    implementation("androidx.room:room-guava:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")
    implementation("androidx.room:room-paging:$room_version")

    implementation("androidx.work:work-runtime:2.11.1")

    // ########################################## compose ##########################################
    val composeBom = platform("androidx.compose:compose-bom:2025.12.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    // Choose one of the following:
    // Material Design 3
    implementation("androidx.compose.material3:material3")
    // or skip Material Design and build directly on top of foundational components
//    implementation("androidx.compose.foundation:foundation")
    // or only import the main APIs for the underlying toolkit systems,
    // such as input and measurement/layout
//    implementation("androidx.compose.ui:ui")
    // Android Studio Preview support
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    // Optional - Add window size utils
    implementation("androidx.compose.material3.adaptive:adaptive")
    // Optional - Integration with activities
    implementation("androidx.activity:activity-compose:1.12.4")
    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")
    // Optional - Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata")
    // Optional - Integration with RxJava
    implementation("androidx.compose.runtime:runtime-rxjava2")

    // ########################################## Retrofit #########################################
    // Retrofit 核心库
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    // Gson 转换器，用于将JSON自动解析为对象
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    // okhttp
    implementation("com.squareup.okhttp3:okhttp:5.3.2")

    // Lifecycle LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.10.0")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")

    // ########################################### Dagger ##########################################
//    // Dagger2 核心库 //
//    implementation("com.google.dagger:dagger:2.59.2")
//    // Dagger2
//    ksp("com.google.dagger:dagger-compiler:2.59.2")
//
//    // (可选) 如果你需要在 Android 组件（如 Activity/Fragment）中使用 Dagger
//    implementation("com.google.dagger:dagger-android:2.59.2")
//    implementation("com.google.dagger:dagger-android-support:2.59.2") // 如果使用 AndroidX
//    ksp("com.google.dagger:dagger-android-processor:2.59.2")

    // ########################################### Hilt ############################################
    // Hilt
    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-compiler:2.59.2")
    // 如需使用 Hilt 的 ViewModel 扩展
    implementation("androidx.hilt:hilt-navigation-fragment:1.3.0")

    // ################################### Fragment navigation #####################################
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.7")
}