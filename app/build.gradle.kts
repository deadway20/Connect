
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("/home/user/Downloads/Coder-x.jks")
            storePassword = "Yousef511?"
            keyAlias = "key0"
            keyPassword = "Yousef511?"
        }
    }
    namespace = "com.coder_x.connect"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.coder_x.connect"
        minSdk = 29
        targetSdk = 34
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
    kotlinOptions {
        jvmTarget = "11"
    }
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.kotlin.stdlib)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.jtds)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.glide)
    implementation(libs.squareup.picasso)
    implementation(libs.androidx.ui.text.android)
    implementation(libs.ucrop)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)

    // Room dependencies:
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    //sound Graph view Visualizer
    implementation(libs.waveformseekbar)
    implementation("com.github.lincollincol:amplituda:2.2.2")






    // expandable Calendar view
    implementation(libs.view)

    //lottie animation
    implementation(libs.lottie)

    // مكتبة قوية جداً لإنشاء الرسوم البيانية
    implementation(libs.mpandroidchart)

}