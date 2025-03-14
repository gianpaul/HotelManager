package exirium.pe.app.shared.feature.auth.presentation.mvi

import exirium.pe.app.shared.core.presentation.mvi.Reducer

class AuthReducer : Reducer<AuthState, AuthIntent> {
    override fun reduce(state: AuthState, intent: AuthIntent): AuthState {
        return when (intent) {
            is AuthIntent.SignIn -> state.copy(
                isLoading = true,
                error = null,
                passwordResetSent = false
            )

            is AuthIntent.SignUp -> state.copy(
                isLoading = true,
                error = null,
                passwordResetSent = false
            )

            is AuthIntent.SignOut -> state.copy(
                isLoading = true,
                error = null,
                passwordResetSent = false
            )

            is AuthIntent.ResetPassword -> state.copy(
                isLoading = true,
                error = null,
                passwordResetSent = false
            )

            is AuthIntent.CheckAuthState -> state.copy(
                isLoading = true,
                error = null
            )
        }
    }
}