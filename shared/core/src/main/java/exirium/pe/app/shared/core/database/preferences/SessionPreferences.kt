package exirium.pe.app.shared.core.database.preferences

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.serialization.json.Json

interface SessionPreferences {
    suspend fun saveSessionToken(token: String, refreshToken: String? = null)
    suspend fun getSessionToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearSession()
    fun hasSession(): Boolean
}

/**
 * Implementación de SessionPreferences utilizando Settings.
 */
class SessionPreferencesImpl(
    private val settings: Settings,
    private val json: Json
) : SessionPreferences {

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    override suspend fun saveSessionToken(token: String, refreshToken: String?) {
        settings[KEY_ACCESS_TOKEN] = token
        refreshToken?.let { settings[KEY_REFRESH_TOKEN] = it }
    }

    override suspend fun getSessionToken(): String? {
        return if (settings.hasKey(KEY_ACCESS_TOKEN)) settings.getString(KEY_ACCESS_TOKEN, "") else null
    }

    override suspend fun getRefreshToken(): String? {
        return if (settings.hasKey(KEY_REFRESH_TOKEN)) settings.getString(KEY_REFRESH_TOKEN, "") else null
    }

    override suspend fun clearSession() {
        settings.remove(KEY_ACCESS_TOKEN)
        settings.remove(KEY_REFRESH_TOKEN)
    }

    override fun hasSession(): Boolean {
        return settings.hasKey(KEY_ACCESS_TOKEN)
    }
}