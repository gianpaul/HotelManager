package exirium.pe.app.shared.core.database.di

import com.russhwolf.settings.Settings
import exirium.pe.app.shared.core.database.preferences.SessionPreferences
import exirium.pe.app.shared.core.database.preferences.SessionPreferencesImpl
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

fun databaseModule(settingsFactory: () -> Settings): Module = module {
    single { settingsFactory() }

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
        }
    }

    // Proporcionar preferencias de sesión
    single<SessionPreferences> { SessionPreferencesImpl(get(), get()) }
}