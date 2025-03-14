package exirium.pe.app.shared.feature.auth.application.usecase

import exirium.pe.app.shared.domain.model.Session
import exirium.pe.app.shared.domain.port.storage.SessionStorage
import exirium.pe.app.shared.feature.auth.domain.port.AuthPort
import kotlinx.coroutines.flow.Flow

class GetSessionUseCase(
    private val authPort: AuthPort,
    private val sessionStorage: SessionStorage
) {

    operator fun invoke(): Flow<Session?> {
        return authPort.sessionFlow()
    }

    suspend fun getCurrentSession(): Session? {
        val session = authPort.getCurrentSession()
        if (session == null) {
            val storedSession = sessionStorage.getSession()
            if (storedSession != null) {
                return authPort.refreshToken().getOrNull()
            }
        }

        return session
    }

    fun isLoggedIn(): Boolean {
        return authPort.isLoggedIn()
    }
}