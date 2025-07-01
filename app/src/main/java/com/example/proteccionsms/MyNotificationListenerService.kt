package com.example.proteccionsms // ¡CORREGIDO! Asegúrate de que este sea el nombre de tu paquete

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject // Necesario para manejar JSON
import kotlinx.coroutines.cancel // ¡IMPORTACIÓN AÑADIDA PARA LA FUNCIÓN CANCEL!
import com.example.proteccionsms.BlockedMessageCounter // ¡CORREGIDO! Importación correcta para BlockedMessageCounter

/**
 * Servicio que escucha las notificaciones del sistema.
 * Intercepta notificaciones, extrae su contenido y aplica lógica de detección de phishing.
 */
class MyNotificationListenerService : NotificationListenerService() {

    // CoroutineScope para manejar las operaciones asíncronas del servicio.
    // SupervisorJob permite que las corrutinas hijas fallen sin cancelar el scope completo.
    // Dispatchers.IO es ideal para operaciones de red y disco.
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val client = OkHttpClient() // Cliente OkHttp para las peticiones de red.

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val packageName = it.packageName
            // Extraer el título y el texto de la notificación
            val title = it.notification.extras.getString("android.title") ?: ""
            val text = it.notification.extras.getString("android.text") ?: ""

            val notificationInfo = "App: $packageName\nTitulo: $title\nMensaje: $text"
            Log.d("NotificationListener", "Notificación Recibida: $notificationInfo")

            // Lanzar una corrutina para realizar la detección de phishing, incluyendo la llamada a la API.
            // Las operaciones de red no pueden ejecutarse en el hilo principal (UI thread).
            serviceScope.launch {
                val isPhishing = shouldBlockNotification(packageName, title, text)
                if (isPhishing) {
                    Log.d("NotificationListener", "Notificación bloqueada (Phishing): $notificationInfo")
                    BlockedMessageCounter.incrementBlockedNotificationCount() // Incrementar contador de notificaciones bloqueadas
                    // Opcional: Puedes decidir si quieres cancelar la notificación para que no se muestre al usuario.
                    // Esto es una acción fuerte y debe usarse con precaución.
                    // cancelNotification(sbn.key)
                }

                // Enviar un broadcast a la MainActivity para actualizar la UI con la última notificación.
                // Asegurarse de que las actualizaciones de UI se envíen al hilo principal.
                withContext(Dispatchers.Main) {
                    // Asegúrate de que el nombre del paquete en el Intent coincida con tu paquete principal
                    val intent = Intent("com.example.proteccionsms.NOTIFICATION_RECEIVED_ACTION") // ¡CORREGIDO! Verifica este paquete
                    intent.putExtra("notification_text", notificationInfo)
                    sendBroadcast(intent)
                }
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let {
            Log.d("NotificationListener", "Notificación Removida: ${it.packageName}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Es crucial cancelar todas las corrutinas cuando el servicio se destruye
        // para evitar fugas de memoria y trabajo innecesario.
        serviceScope.cancel()
    }

    /**
     * Determina si una notificación debe ser considerada phishing.
     * Incluye detección por palabras clave locales y verificación a través de una API externa.
     * Esta función es 'suspend' porque realiza una operación de red asíncrona.
     * @param packageName El nombre del paquete de la aplicación que envió la notificación.
     * @param title El título de la notificación.
     * @param text El texto principal de la notificación.
     * @return true si la notificación contiene palabras clave de phishing o es marcada como maliciosa por la API, false en caso contrario.
     */
    private suspend fun shouldBlockNotification(packageName: String, title: String, text: String): Boolean {
        // Lista de palabras clave comunes en ataques de phishing (puedes expandirla)
        val phishingKeywords = listOf(
            "actualice su informacion", "su cuenta ha sido bloqueada", "verifique su identidad",
            "ganador de la loteria", "premio", "haga clic aqui", "urgente", "suspension",
            "problemas con su pago", "envio pendiente", "reembolso", "paquete retenido",
            "acceso no autorizado", "restablecer contraseña", "verifique su cuenta",
            "datos bancarios", "tarjeta de credito", "descuento exclusivo", "oferta limitada",
            "confirmar datos", "inicia sesion", "verificacion de seguridad"
        )

        // Combinar título y texto para la detección local
        val fullNotificationContent = "$title $text"

        // 1. Verificación local por palabras clave
        if (phishingKeywords.any { fullNotificationContent.contains(it, ignoreCase = true) }) {
            Log.d("PhishingDetector", "Detección local: Palabras clave de phishing encontradas.")
            return true
        }

        // 2. Verificación a través de API externa
        return try {
            val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val jsonBody = JSONObject().apply {
                put("mensaje", fullNotificationContent)
            }.toString()

            val requestBody = jsonBody.toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url("https://mmtd.twoko.io/verificarMensaje") // <-- Aquí está la URL de la API
                .header("Content-Type", "application/json")
                .header("X-API-Key", "tokensecreto123") // <-- Aquí está la clave API
                .post(requestBody)
                .build()

            // Ejecutar la petición en el hilo de IO y esperar la respuesta
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                Log.d("PhishingDetector", "Respuesta API: $responseBody")
                responseBody?.let {
                    val jsonResponse = JSONObject(it)
                    val resultado = jsonResponse.optString("resultado") // Usar optString para evitar errores si la clave no existe
                    if (resultado == "Malicioso") {
                        Log.d("PhishingDetector", "Detección API: Mensaje marcado como malicioso.")
                        true
                    } else {
                        Log.d("PhishingDetector", "Detección API: Mensaje marcado como legítimo.")
                        false
                    }
                } ?: false // Si el cuerpo de la respuesta es nulo, no se bloquea
            } else {
                Log.e("PhishingDetector", "Error en la llamada a la API: ${response.code} - ${response.message}")
                false // No bloquear si hay un error en la API (podría ser un problema de red, no de phishing)
            }
        } catch (e: Exception) {
            Log.e("PhishingDetector", "Excepción al llamar a la API de phishing: ${e.message}", e)
            false // No bloquear si hay una excepción (por ejemplo, sin conexión a internet)
        }
    }
}
