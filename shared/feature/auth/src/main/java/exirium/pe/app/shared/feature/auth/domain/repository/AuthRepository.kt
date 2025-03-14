package exirium.pe.app.shared.feature.auth.domain.repository

import exirium.pe.app.shared.feature.auth.domain.model.AuthResult
import exirium.pe.app.shared.feature.auth.domain.model.Credentials
import exirium.pe.app.shared.feature.auth.domain.model.RegistrationData
import exirium.pe.app.shared.feature.auth.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(credentials: Credentials): Result<AuthResult>
    suspend fun signUp(data: RegistrationData): Result<AuthResult>
    suspend fun signOut(): Result<Unit>
    fun isLoggedIn(): Boolean
    suspend fun getCurrentUser(): User?
    fun observeCurrentUser(): Flow<User?>
    suspend fun getUserById(id: String): Result<User?>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun updateUserProfile(user: User): Result<User>
}