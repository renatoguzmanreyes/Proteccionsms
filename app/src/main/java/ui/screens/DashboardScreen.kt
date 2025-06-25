package com.example.proteccionsms.ui.screens // ¡MUY IMPORTANTE! Verifica que este paquete sea CORRECTO

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proteccionsms.ui.theme.ProteccionsmsTheme // ¡MUY IMPORTANTE! Verifica esta importación
import androidx.compose.material.icons.Icons // Para usar los iconos de Material Design
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.Alignment // Para alinear contenido dentro de Card
import androidx.compose.ui.graphics.Color // Para usar colores directamente

// >>> Nuevas importaciones para el scroll <<<
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

// -----------------------------------------------------------
// Pantalla Principal del Dashboard
// -----------------------------------------------------------
@Composable
fun DashboardScreen(
    smsBlockedCount: Int,
    notificationsInterceptedCount: Int,
    onViewSmsLog: () -> Unit, // Callback para navegar al log de SMS
    onViewNotificationLog: () -> Unit, // Callback para navegar al log de Notificaciones
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState() // Crea un estado para controlar el scroll

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Usa el color de fondo de tu tema
            .verticalScroll(scrollState) // ¡AÑADIDO! Habilita el scroll vertical para este Column
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre las tarjetas
    ) {
        // SMS Bloqueados Card
        DashboardCard(
            title = "SMS Bloqueados",
            value = smsBlockedCount.toString(),
            description = "Mensajes SMS sospechosos detectados y bloqueados.",
            icon = Icons.Default.Email, // Icono de Material Design para SMS
            buttonText = "Ver Registro",
            onButtonClick = onViewSmsLog // Pasamos el callback
        )

        // Notificaciones Interceptadas Card
        DashboardCard(
            title = "Notificaciones Interceptadas",
            value = notificationsInterceptedCount.toString(),
            description = "Notificaciones push sospechosas interceptadas.",
            icon = Icons.Default.Notifications, // Icono de Material Design para Notificaciones
            buttonText = "Ver Registro",
            onButtonClick = onViewNotificationLog // Pasamos el callback
        )

        // Protección Activa Card (como en tu código React)
        DashboardCard(
            title = "Protección Activa",
            value = "24/7",
            description = "Tu dispositivo está protegido contra phishing.",
            icon = Icons.Default.Security, // Icono de seguridad
            buttonText = null, // No hay botón para este card
            onButtonClick = null // No hay acción de click
        )
    }
}

// -----------------------------------------------------------
// Componente Reutilizable para las Tarjetas del Dashboard
// -----------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class) // Necesario para CardDefaults
@Composable
fun DashboardCard(
    title: String,
    value: String,
    description: String,
    icon: ImageVector, // Para usar iconos de Material Design
    buttonText: String?, // El texto del botón (puede ser nulo si no hay botón)
    onButtonClick: (() -> Unit)? // La acción del botón (puede ser nula)
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp), // Ajusta la altura mínima para que el botón quepa
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Usa el color de superficie de tu tema (dark_card_surface)
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Sombra de la tarjeta
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start, // Alinea el contenido a la izquierda
            verticalArrangement = Arrangement.SpaceBetween // Distribuye el espacio verticalmente
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // Descripción de contenido para accesibilidad
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f), // Color del icono (blanco semi-transparente)
                modifier = Modifier.size(48.dp) // Tamaño del icono
            )
            Spacer(modifier = Modifier.height(12.dp)) // Espacio vertical
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall, // Estilo de texto del tema
                color = MaterialTheme.colorScheme.onSurface // Color del texto (blanco)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.displayMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                // Ajusta el tamaño de la fuente para que quepa bien
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) // Blanco semi-transparente para la descripción
            )
            if (buttonText != null && onButtonClick != null) { // Si hay texto y acción para el botón
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onButtonClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), // Color primario de tu tema
                    modifier = Modifier.fillMaxWidth() // Botón que ocupa todo el ancho
                ) {
                    Text(buttonText, color = MaterialTheme.colorScheme.onPrimary) // Texto blanco en el botón morado
                }
            }
        }
    }
}

// -----------------------------------------------------------
// Preview del Dashboard (para ver cómo se ve sin ejecutar la app)
// -----------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    ProteccionsmsTheme { // Asegúrate de que este sea el nombre de tu tema
        DashboardScreen(
            smsBlockedCount = 123,
            notificationsInterceptedCount = 45,
            onViewSmsLog = {}, // Acciones vacías para el preview
            onViewNotificationLog = {} // Acciones vacías para el preview
        )
    }
}