package exirium.pe.app.shared.feature.auth.presentation.mvi

import exirium.pe.app.shared.core.presentation.mvi.Processor
import exirium.pe.app.shared.feature.auth.application.usecase.GetSessionUseCase
import exirium.pe.app.shared.feature.auth.application.usecase.ResetPasswordUseCase
import exirium.pe.app.shared.feature.auth.application.usecase.SignInUseCase
import exirium.pe.app.shared.feature.auth.application.usecase.SignOutUseCase
import exirium.pe.app.shared.feature.auth.application.usecase.SignUpUseCase
import exirium.pe.app.shared.feature.auth.domain.error.AuthError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthProcessor(
    private val signInUseCase: SignInUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getSessionUseCase: GetSessionUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : Processor<AuthIntent, AuthEffect> {

    override suspend fun process(intent: AuthIntent): Flow<AuthEffect> = flow {
        when (intent) {
            is AuthIntent.SignIn -> {
                signInUseCase(intent.credentials)
                    .onSuccess {
                        emit(AuthEffect.NavigateToDashboard)
                    }
                    .onFailure { error ->
                        val errorMessage = when (error) {
                            is AuthError.InvalidCredentials -> "Credenciales incorrectas"
                            is NetworkError.ConnectionError -> "Error de conexión"
                            else -> error.message ?: "Error desconocido"
                        }
                        emit(AuthEffect.ShowError(errorMessage))
                    }
            }

            is AuthIntent.SignUp -> {
                signUpUseCase(intent.registration)
                    .onSuccess {
                        emit(AuthEffect.NavigateToDashboard)
                        emit(AuthEffect.ShowSuccess("Registro exitoso"))
                    }
                    .onFailure { error ->
                        val errorMessage = when (error) {
                            is AuthError.EmailAlreadyExists -> "El email ya está registrado"
                            is AuthError.WeakPassword -> "La contraseña es demasiado débil"
                            is NetworkError.ConnectionError -> "Error de conexión"
                            else -> error.message ?: "Error desconocido"
                        }
                        emit(AuthEffect.ShowError(errorMessage))
                    }
            }

            is AuthIntent.SignOut -> {
                signOutUseCase()
                    .onSuccess {
                        emit(AuthEffect.NavigateToLogin)
                    }
                    .onFailure { error ->
                        emit(AuthEffect.ShowError(error.message ?: "Error al cerrar sesión"))
                    }
            }

            is AuthIntent.ResetPassword -> {
                resetPasswordUseCase(intent.email)
                    .onSuccess {
                        emit(AuthEffect.ShowSuccess(
                            "Se ha enviado un enlace de restablecimiento a tu correo"
                        ))
                    }
                    .onFailure { error ->
                        emit(AuthEffect.ShowError(
                            error.message ?: "Error al enviar el enlace de restablecimiento"
                        ))
                    }
            }

            is AuthIntent.CheckAuthState -> {
                val isLoggedIn = getSessionUseCase.isLoggedIn()
                if (isLoggedIn) {
                    emit(AuthEffect.NavigateToDashboard)
                } else {
                    emit(AuthEffect.NavigateToLogin)
                }
            }
        }
    }
}