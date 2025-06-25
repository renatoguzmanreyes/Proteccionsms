plugins {
    // Alias de plugins para usar en build.gradle.kts (Module: app)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false // Importante si usas Compose
}