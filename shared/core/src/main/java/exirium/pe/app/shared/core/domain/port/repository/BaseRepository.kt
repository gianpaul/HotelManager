package exirium.pe.app.shared.core.domain.port.repository

import kotlinx.coroutines.flow.Flow

interface BaseRepository<T, ID> {
    fun getAll(): Flow<Result<List<T>>>
    fun getById(id: ID): Flow<Result<T?>>
    suspend fun create(entity: T): Result<T>
    suspend fun update(entity: T): Result<T>
    suspend fun delete(id: ID): Result<Boolean>
}