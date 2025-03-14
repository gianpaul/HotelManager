package exirium.pe.app.shared.feature.auth.presentation.mvi

import exirium.pe.app.shared.core.presentation.mvi.State
import exirium.pe.app.shared.domain.model.Session

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val session: Session? = null,
    val error: String? = null,
    val passwordResetSent: Boolean = false
) : State