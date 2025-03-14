package exirium.pe.app.shared.feature.auth.data.source.remote

import exirium.pe.app.shared.feature.auth.data.source.remote.dto.UserDto
import exirium.pe.app.shared.feature.auth.domain.model.UserRole

interface AuthRemoteDataSource {
    suspend fun signInWithEmail(email: String, password: String): Result<UserDto>
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String,
        role: UserRole
    ): Result<UserDto>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): UserDto?
    suspend fun refreshSession(): Result<UserDto?>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun saveUserProfile(userDto: UserDto): Result<UserDto>
    suspend fun getUserById(id: String): Result<UserDto?>
}