plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // KSP alineado con Kotlin 2.0.21
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.monsterapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.monsterapp"
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
    kotlinOptions {
        jvmTarget = "11"
    }

    // Activamos ViewBinding para acceder a las vistas de forma segura
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Retrofit - Cliente HTTP para Android
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    // Moshi - Librería para parsear JSON de forma eficiente
    implementation("com.squareup.moshi:moshi:1.15.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // Conversor de Retrofit para usar Moshi
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0")

    // Interceptor de logging para ver las peticiones HTTP en Logcat
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ViewModel con soporte para Coroutines
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // LiveData KTX - Para usar asLiveData() con Flow
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")

    // Extensión para usar viewModels() en Activity
    implementation("androidx.activity:activity-ktx:1.8.2")

    // Extensión para fragments (commit, viewModels, etc.)
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Navigation Component - Para navegación entre fragments
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // OSMDroid - Mapas OpenStreetMap (alternativa open-source a Google Maps)
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Glide - Carga de imágenes desde URL
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
