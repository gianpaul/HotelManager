package exirium.pe.app.shared.feature.auth.application.usecase

import exirium.pe.app.shared.domain.model.Session
import exirium.pe.app.shared.domain.port.storage.SessionStorage
import exirium.pe.app.shared.feature.auth.domain.model.Registration
import exirium.pe.app.shared.feature.auth.domain.port.AuthPort

class SignUpUseCase(
    private val authPort: AuthPort,
    private val sessionStorage: SessionStorage
) {
    suspend operator fun invoke(registration: Registration): Result<Session> {
        return authPort.signUp(registration).onSuccess { session ->
            // Guarda la sesión en almacenamiento local
            sessionStorage.saveSession(session)
        }
    }
}