package exirium.pe.app.shared.core.network.di

import exirium.pe.app.shared.core.network.SupabaseClientProvider
import exirium.pe.app.shared.core.network.SupabaseConfig
import org.koin.core.module.Module
import org.koin.dsl.module

fun networkModule(
    supabaseUrl: String,
    supabaseKey: String,
    authCallbackHost: String? = null
): Module = module {
    single {
        SupabaseConfig(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey,
            authCallbackHost = authCallbackHost
        )
    }

    single { SupabaseClientProvider(get()) }
}
