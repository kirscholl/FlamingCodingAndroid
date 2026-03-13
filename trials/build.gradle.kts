plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}
val buildMode = project.findProperty("buildMode")
val isRelease = buildMode == "release"

// 动态应用插件 (切记不能写在 plugins {} 闭包里)
//if (isRelease) {
//    apply(plugin = "com.android.library")
////    plugins.apply("com.android.library")
//} else {
//    apply(plugin = "com.android.application")
////    plugins.apply("com.android.application")
//}
////plugins.apply("org.jetbrains.kotlin.android")
//apply(plugin = "org.jetbrains.kotlin.android")

android {
    namespace = "com.example.trialsapplication"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        // 只有在 App 模式下才需要 applicationId
        if (!isRelease) {
            applicationId = "com.example.trialsapplication"
        }
        applicationId = "com.example.trialsapplication"
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

    // 6. 动态配置 SourceSets (清单文件区分)
    sourceSets {
        getByName("main") {
            if (!isRelease) {
                // 作为 App 运行时，使用带 Launcher Activity 的 Manifest
                manifest.srcFile("src/main/debug/AndroidManifest.xml")
            } else {
                // 作为 Library 运行时，使用普通的 Manifest (没有 Application 和 Launcher)
                manifest.srcFile("src/main/AndroidManifest.xml")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}