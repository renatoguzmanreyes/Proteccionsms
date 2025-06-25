package com.example.proteccionsms // ¡MUY IMPORTANTE! Verifica que este paquete sea CORRECTO para tu proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proteccionsms.ui.screens.DashboardScreen // ¡IMPORTA TU DASHBOARD SCREEN!
import com.example.proteccionsms.ui.theme.ProteccionsmsTheme // ¡Verifica que esta importación sea CORRECTA!

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProteccionsmsTheme { // Asegúrate que el nombre del tema aquí coincide con el de tu Theme.kt
                // Una superficie que ocupa toda la pantalla y usa el color de fondo de nuestro tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // Esto tomará el dark_background de tu tema
                ) {
                    // Aquí mostramos la pantalla de tu Dashboard
                    DashboardScreen(
                        smsBlockedCount = 123, // Valor de ejemplo
                        notificationsInterceptedCount = 45, // Valor de ejemplo
                        onViewSmsLog = {
                            // Aquí irá la lógica para navegar a la pantalla de registro de SMS
                            // Por ahora, podemos imprimir un mensaje en Logcat
                            println("Navegar a Registro SMS")
                        },
                        onViewNotificationLog = {
                            // Aquí irá la lógica para navegar a la pantalla de registro de Notificaciones
                            // Por ahora, podemos imprimir un mensaje en Logcat
                            println("Navegar a Registro Notificaciones")
                        }
                    )
                }
            }
        }
    }
}

// Opcional: El Preview de MainActivity, si quieres que se vea el Dashboard
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    ProteccionsmsTheme {
        DashboardScreen(
            smsBlockedCount = 123,
            notificationsInterceptedCount = 45,
            onViewSmsLog = {},
            onViewNotificationLog = {}
        )
    }
}