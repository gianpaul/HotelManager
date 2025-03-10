package exirium.pe.app.shared.domain.repository

import exirium.pe.app.shared.domain.model.User

interface AuthRepository {
    suspend fun login(email: String, password: String, hotelId: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun isLoggedIn(): Boolean
}