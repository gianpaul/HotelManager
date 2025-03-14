package exirium.pe.app.shared.feature.auth.domain.port

import exirium.pe.app.shared.feature.auth.domain.model.Credentials
import exirium.pe.app.shared.feature.auth.domain.model.Registration
import exirium.pe.app.shared.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface AuthPort {
    suspend fun signIn(credentials: Credentials): Result<Session>
    suspend fun signUp(registration: Registration): Result<Session>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun getCurrentSession(): Session?
    fun sessionFlow(): Flow<Session?>
    fun isLoggedIn(): Boolean
    suspend fun refreshToken(): Result<Session>
}