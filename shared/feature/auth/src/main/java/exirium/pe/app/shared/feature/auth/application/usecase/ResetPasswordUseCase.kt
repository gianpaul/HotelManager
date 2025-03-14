package exirium.pe.app.shared.feature.auth.application.usecase

import exirium.pe.app.shared.feature.auth.domain.port.AuthPort

class ResetPasswordUseCase(
    private val authPort: AuthPort
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        return authPort.resetPassword(email)
    }
}