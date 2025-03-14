package exirium.pe.app.shared.feature.auth.application.usecase

import exirium.pe.app.shared.domain.model.Session
import exirium.pe.app.shared.domain.port.storage.SessionStorage
import exirium.pe.app.shared.feature.auth.domain.port.AuthPort

class RefreshTokenUseCase(
    private val authPort: AuthPort,
    private val sessionStorage: SessionStorage
) {
    suspend operator fun invoke(): Result<Session> {
        return authPort.refreshToken().onSuccess { session ->
            // Actualiza la sesión en almacenamiento local
            sessionStorage.saveSession(session)
        }
    }
}