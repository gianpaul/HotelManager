package exirium.pe.app.shared.feature.auth.presentation.mvi

import exirium.pe.app.shared.core.presentation.mvi.Intent
import exirium.pe.app.shared.feature.auth.domain.model.Credentials
import exirium.pe.app.shared.feature.auth.domain.model.Registration

sealed class AuthIntent : Intent {
    data class SignIn(val credentials: Credentials) : AuthIntent()
    data class SignUp(val registration: Registration) : AuthIntent()
    data object SignOut : AuthIntent()
    data class ResetPassword(val email: String) : AuthIntent()
    data object CheckAuthState : AuthIntent()
}
