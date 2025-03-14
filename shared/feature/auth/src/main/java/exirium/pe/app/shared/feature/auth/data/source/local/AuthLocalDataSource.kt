package exirium.pe.app.shared.feature.auth.data.source.local

import exirium.pe.app.shared.feature.auth.data.source.remote.dto.UserDto
import kotlinx.coroutines.flow.Flow

interface AuthLocalDataSource {
    suspend fun saveCurrentUser(user: UserDto)

    suspend fun getCurrentUser(): UserDto?

    fun getCurrentUserSync(): UserDto?

    suspend fun clearCurrentUser()

    fun observeCurrentUser(): Flow<UserDto?>
}