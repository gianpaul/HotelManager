package exirium.pe.app.shared.feature.auth.data.source.local

import com.russhwolf.settings.Settings
import exirium.pe.app.shared.feature.auth.data.source.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

class AuthLocalDataSourceImpl(
    private val settings: Settings, private val json: Json
) : AuthLocalDataSource {

    companion object {
        private const val KEY_CURRENT_USER = "current_user"
    }

    /**
     * Guarda el usuario actual en almacenamiento local
     */
    override suspend fun saveCurrentUser(user: UserDto) {
        val userJson = json.encodeToString(user)
        settings.putString(KEY_CURRENT_USER, userJson)
    }

    /**
     * Obtiene el usuario actual del almacenamiento local
     */
    override suspend fun getCurrentUser(): UserDto? {
        val userJson = settings.getStringOrNull(KEY_CURRENT_USER) ?: return null
        return try {
            json.decodeFromString<UserDto>(userJson)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtiene el usuario actual de forma síncrona
     */
    override fun getCurrentUserSync(): UserDto? {
        val userJson = settings.getStringOrNull(KEY_CURRENT_USER) ?: return null
        return try {
            json.decodeFromString<UserDto>(userJson)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Elimina el usuario actual del almacenamiento local
     */
    override suspend fun clearCurrentUser() {
        settings.remove(KEY_CURRENT_USER)
    }

    /**
     * Observa cambios en el usuario actual
     * Implementación específica para cada plataforma
     */
    override fun observeCurrentUser(): Flow<UserDto?> {
        // Esta implementación es esquemática y debería adaptarse según la plataforma
        // En Android podría usarse DataStore.data.map
        // En iOS podríamos usar NSUserDefaultsObserver
        return settings.observeString(KEY_CURRENT_USER, null).map { userJson ->
                if (userJson != null) {
                    try {
                        json.decodeFromString<UserDto>(userJson)
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    null
                }
            }
    }

    // Extensión para obtener string nullable de Settings
    private fun Settings.getStringOrNull(key: String): String? {
        return if (this.hasKey(key)) this.getString(key, "") else null
    }
}

/**
 * Extension function para observar cambios en un valor de Settings.
 * Esta es una implementación esquemática que deberá adaptarse a cada plataforma.
 */
fun Settings.observeString(key: String, defaultValue: String?): Flow<String?> {
    // Implementación específica para la plataforma
    // Esta es una implementación de ejemplo que siempre devuelve el valor actual
    // En la realidad, deberías implementar un mecanismo de observación específico para cada plataforma
    return kotlinx.coroutines.flow.flow {
        while (true) {
            val value = if (hasKey(key)) getString(key, defaultValue ?: "") else defaultValue
            emit(value)
            kotlinx.coroutines.delay(1000) // Sólo para el ejemplo - no hagas esto en producción
        }
    }
}