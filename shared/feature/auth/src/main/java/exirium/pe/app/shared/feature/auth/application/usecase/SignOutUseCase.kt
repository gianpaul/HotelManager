package exirium.pe.app.shared.feature.auth.application.usecase

import exirium.pe.app.shared.domain.port.storage.SessionStorage
import exirium.pe.app.shared.feature.auth.domain.port.AuthPort

class SignOutUseCase(
    private val authPort: AuthPort,
    private val sessionStorage: SessionStorage
) {
    suspend operator fun invoke(): Result<Unit> {
        return authPort.signOut().onSuccess {
            sessionStorage.clearSession()
        }
    }
}