plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Asegúrate de que este plugin esté correctamente configurado en libs.versions.toml si lo usas con alias
    // Si no, podría ser 'org.jetbrains.kotlin.android.compose' o 'kotlin-compose' directamente.
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.proteccionsms" // Vuelve a verificar que este namespace sea correcto
    compileSdk = 35 // Mantén tu compileSdk actual (puede ser 34 o 35)

    defaultConfig {
        applicationId = "com.example.proteccionsms" // Vuelve a verificar que este ID sea correcto
        minSdk = 24 // Tu minSdk
        targetSdk = 35 // Tu targetSdk (debería coincidir con compileSdk)
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
    buildFeatures {
        // Habilita Jetpack Compose para este módulo
        compose = true
    }
    composeOptions {
        // Establece la versión de Kotlin Compiler para Compose
        // Asegúrate que esta versión sea compatible con la de tu Kotlin (puedes verificar en el build.gradle.kts de nivel de proyecto)
        kotlinCompilerExtensionVersion = "1.5.11" // O la versión que Android Studio te sugiera automáticamente
    }
}

dependencies {
    // Dependencias básicas que ya tenías y son necesarias para la app Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Jetpack Compose BOM (Bill of Materials) - IMPORTANTE para gestionar versiones de Compose
    implementation(platform(libs.androidx.compose.bom))

    // Dependencias de UI de Jetpack Compose
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // Dependencia explícita para Material3, con una versión específica para asegurar compatibilidad
    // Si usas el BOM, a menudo no necesitas especificar la versión aquí.
    implementation("androidx.compose.material3:material3:1.2.1")

    // Dependencia para los iconos extendidos de Material Design (Email, Notifications, Security)
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    // >>>>>>> DEPENDENCIA CRUCIAL PARA OBSERVAR LIVEDATA EN COMPOSE <<<<<<<
    // Necesaria para usar .observeAsState() con LiveData en funciones @Composable
    implementation("androidx.compose.runtime:runtime-livedata:1.6.8") // Versión compatible con Compose BOM 2023.08.00 o más reciente

    // >>>>>>> NUEVA DEPENDENCIA PARA COMPOSEN NAVIGATION <<<<<<<
    // Esta dependencia está incluida, pero asegúrate de que tu código Compose (ej. DashboardScreen)
    // realmente la esté utilizando para la navegación. Si no, puedes comentarla por ahora.
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Dependencias de testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Herramientas de depuración para Compose UI (útiles para previsualizaciones en Android Studio)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Para peticiones de red (OkHttp)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Para Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}