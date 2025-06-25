package com.example.proteccionsms.ui.theme // ESTA LÍNEA ES CORRECTA SEGÚN TU CAPTURA

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.Color

// Definimos el esquema de color oscuro utilizando valores Color(0xFF...) directamente.
// Esto es para evitar errores de contexto @Composable y usar tus colores
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF673AB7),        // primary_purple
    onPrimary = Color.White,
    primaryContainer = Color(0xFF673AB7),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF9C27B0),      // secondary_purple
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF9C27B0),
    onSecondaryContainer = Color.White,
    background = Color(0xFF24273F),     // dark_background
    onBackground = Color.White,
    surface = Color(0xFF33364F),        // dark_card_surface
    onSurface = Color.White,
    error = Color(0xFFCF6679),
    onError = Color.Black
)

// Esquema de color claro (por si acaso, aunque no lo usaremos activamente)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),
    onPrimary = Color.White,
    secondary = Color(0xFF03DAC5),
    onSecondary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Color(0xFFB00020),
    onError = Color.White
)

@Composable
fun ProteccionsmsTheme( // CONFIRMA QUE ESTE ES EL NOMBRE DE LA FUNCIÓN DE TU TEMA (normalmente nombre_proyecto + Theme)
    darkTheme: Boolean = true, // Forzamos el tema oscuro
    dynamicColor: Boolean = false, // Deshabilitamos el color dinámico
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Asegúrate de que Typography está definida en Typography.kt (viene por defecto)
        content = content
    )
}