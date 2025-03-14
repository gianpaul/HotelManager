package exirium.pe.app.shared.feature.auth.application.usecase

import exirium.pe.app.shared.domain.model.Session
import exirium.pe.app.shared.domain.port.storage.SessionStorage
import exirium.pe.app.shared.feature.auth.domain.model.Credentials
import exirium.pe.app.shared.feature.auth.domain.port.AuthPort

class SignInUseCase(
    private val authPort: AuthPort,
    private val sessionStorage: SessionStorage
) {
    suspend operator fun invoke(credentials: Credentials): Result<Session> {
        return authPort.signIn(credentials).onSuccess { session ->
            sessionStorage.saveSession(session)
        }
    }
}