package exirium.pe.app.shared.feature.auth.domain.di

import exirium.pe.app.shared.feature.auth.domain.usecase.GetCurrentUserUseCase
import exirium.pe.app.shared.feature.auth.domain.usecase.ResetPasswordUseCase
import exirium.pe.app.shared.feature.auth.domain.usecase.SignInUseCase
import exirium.pe.app.shared.feature.auth.domain.usecase.SignOutUseCase
import exirium.pe.app.shared.feature.auth.domain.usecase.SignUpUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

fun authDomainModule(): Module = module {
    factory { SignInUseCase(get()) }
    factory { SignUpUseCase(get()) }
    factory { SignOutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { ResetPasswordUseCase(get()) }
}