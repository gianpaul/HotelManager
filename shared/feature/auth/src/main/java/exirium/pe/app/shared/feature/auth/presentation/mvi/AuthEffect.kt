package exirium.pe.app.shared.feature.auth.presentation.mvi

import exirium.pe.app.shared.core.presentation.mvi.Effect

sealed class AuthEffect : Effect {
    data object NavigateToDashboard : AuthEffect()
    data object NavigateToLogin : AuthEffect()
    data class ShowError(val message: String) : AuthEffect()
    data class ShowSuccess(val message: String) : AuthEffect()
}