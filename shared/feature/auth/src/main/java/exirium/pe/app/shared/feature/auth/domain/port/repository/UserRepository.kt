package exirium.pe.app.shared.feature.auth.domain.port.repository

import exirium.pe.app.shared.core.domain.port.repository.BaseRepository
import exirium.pe.app.shared.domain.model.User
import exirium.pe.app.shared.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

interface UserRepository : BaseRepository<User, String> {
    fun getByRole(role: UserRole): Flow<Result<List<User>>>
    fun getByEmail(email: String): Flow<Result<User?>>
    suspend fun updateActiveStatus(id: String, isActive: Boolean): Result<User>
    suspend fun updateRole(id: String, role: UserRole): Result<User>
}