package exirium.pe.app.shared.domain.port.storage

import exirium.pe.app.shared.domain.model.Session

interface SessionStorage {
    suspend fun saveSession(session: Session)
    suspend fun getSession(): Session?
    suspend fun clearSession()
}
