package exirium.pe.app.shared.core.network

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage

/**
 * Configuración para la conexión a Supabase.
 */
data class SupabaseConfig(
    val supabaseUrl: String,
    val supabaseKey: String,
    val authCallbackHost: String? = null
)

/**
 * Cliente para interactuar con Supabase.
 * Singleton que encapsula todas las operaciones de Supabase.
 */
class SupabaseClientProvider(private val config: SupabaseConfig) {

    val client: SupabaseClient = createSupabaseClient(
        supabaseUrl = config.supabaseUrl,
        supabaseKey = config.supabaseKey
    ) {
        install(Postgrest)
        install(Auth) {
            config.authCallbackHost?.let {
                defaultRedirectUrl = it
            }
            scheme = null
        }
        install(Realtime)
        install(Storage)
    }

    // Accesos directos a los diferentes servicios
    val auth get() = client.auth
    val db get() = client.postgrest
    val realtime get() = client.realtime
    val storage get() = client.storage
}

