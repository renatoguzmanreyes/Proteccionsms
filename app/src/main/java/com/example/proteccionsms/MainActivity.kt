package com.example.proteccionsms // ¡Asegúrate de que este sea el nombre de tu paquete!

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.provider.Settings
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult // ¡IMPORTACIÓN AÑADIDA!
import androidx.activity.result.contract.ActivityResultContracts // ¡IMPORTACIÓN AÑADIDA!
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proteccionsms.ui.screens.DashboardScreen // ¡IMPORTA TU DASHBOARD SCREEN!
import com.example.proteccionsms.ui.theme.ProteccionsmsTheme // ¡Verifica que esta importación sea CORRECTA!
import androidx.compose.runtime.remember
import androidx.compose.runtime.livedata.observeAsState // Para observar LiveData en Compose
import androidx.compose.runtime.LaunchedEffect // Para ejecutar efectos secundarios, como solicitar permisos
import androidx.lifecycle.MutableLiveData // Importar MutableLiveData

class MainActivity : ComponentActivity() {

    // Para el registro del SmsReceiver, se mantiene fuera de Compose para simplificar
    private val smsReceiver = SmsReceiver()
    private lateinit var notificationUpdateReceiver: NotificationUpdateReceiver // Referencia para el receiver de notificaciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProteccionsmsTheme { // Asegúrate que el nombre del tema aquí coincide con el de tu Theme.kt
                // Una superficie que ocupa toda la pantalla y usa el color de fondo de nuestro tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background // Esto tomará el dark_background de tu tema
                ) {
                    // Observar los LiveData de los contadores y últimos mensajes
                    val smsBlockedCount = BlockedMessageCounter.blockedSmsCount.observeAsState(0)
                    val notificationsInterceptedCount = BlockedMessageCounter.blockedNotificationCount.observeAsState(0)
                    val lastSms = SmsReceiver.lastSmsMessage.observeAsState("Esperando SMS...")
                    // Observar el nuevo LiveData para la última notificación
                    val lastNotification = BlockedMessageCounter.lastNotificationMessage.observeAsState("Esperando Notificación...")

                    // Solicitar permisos al inicio de la aplicación o cuando sea necesario
                    val requestPermissionsLauncher =
                        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                            val smsGranted = permissions[Manifest.permission.RECEIVE_SMS] == true &&
                                    permissions[Manifest.permission.READ_SMS] == true
                            // Aquí puedes actualizar un estado Compose para reflejar el permiso SMS
                            // Por ejemplo, un MutableState<Boolean> para smsPermissionsGranted
                        }

                    LaunchedEffect(Unit) {
                        // Solicitar permisos de SMS al iniciar la app
                        requestPermissionsLauncher.launch(
                            arrayOf(
                                Manifest.permission.RECEIVE_SMS,
                                Manifest.permission.READ_SMS
                            )
                        )
                        // Abrir la configuración para el permiso de Notification Listener
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        startActivity(intent)
                    }


                    // Aquí mostramos la pantalla de tu Dashboard
                    DashboardScreen(
                        smsBlockedCount = smsBlockedCount.value,
                        notificationsInterceptedCount = notificationsInterceptedCount.value,
                        lastSmsMessage = lastSms.value,
                        lastNotificationMessage = lastNotification.value, // Ahora usa el LiveData real
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

    override fun onStart() {
        super.onStart()
        // Registrar el BroadcastReceiver para actualizar la UI desde SMS
        val smsIntentFilter = IntentFilter("com.example.proteccionsms.SMS_RECEIVED_ACTION")
        registerReceiver(smsReceiver, smsIntentFilter, RECEIVER_EXPORTED)

        // Registrar el BroadcastReceiver para actualizar la UI desde NotificationListenerService
        val notificationUpdateFilter = IntentFilter("com.example.proteccionsms.NOTIFICATION_RECEIVED_ACTION")
        notificationUpdateReceiver = NotificationUpdateReceiver { s: String ->
            // Actualizar el LiveData en BlockedMessageCounter cuando se recibe una notificación
            BlockedMessageCounter.lastNotificationMessage.postValue(s)
        }
        registerReceiver(notificationUpdateReceiver, notificationUpdateFilter, RECEIVER_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsReceiver)
        unregisterReceiver(notificationUpdateReceiver) // Desregistrar el receiver de notificaciones
    }

    companion object {
        // Define el flag para el receiver, necesario para Android 13+
        const val RECEIVER_EXPORTED = PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
    }
}

/**
 * BroadcastReceiver para recibir actualizaciones de notificaciones desde MyNotificationListenerService.
 * @param callback Función lambda que se ejecuta con el texto de la notificación.
 */
class NotificationUpdateReceiver(val callback: (String) -> Unit) : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // ¡Asegúrate de que el nombre del paquete en la acción del Intent coincida!
        if (intent?.action == "com.example.proteccionsms.NOTIFICATION_RECEIVED_ACTION") {
            val notificationText = intent.getStringExtra("notification_text")
            notificationText?.let { callback(it) }
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
            lastSmsMessage = "SMS de prueba",
            lastNotificationMessage = "Notificación de prueba",
            onViewSmsLog = {},
            onViewNotificationLog = {}
        )
    }
}