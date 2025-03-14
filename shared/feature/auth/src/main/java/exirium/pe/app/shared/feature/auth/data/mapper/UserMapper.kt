package exirium.pe.app.shared.feature.auth.data.mapper

import exirium.pe.app.shared.feature.auth.data.source.remote.dto.UserDto
import exirium.pe.app.shared.feature.auth.domain.model.User
import exirium.pe.app.shared.feature.auth.domain.model.UserRole
import java.time.Instant

class UserMapper {
    fun mapToDomain(dto: UserDto): User {
        return User(
            id = dto.id,
            email = dto.email,
            username = dto.username,
            fullName = dto.fullName,
            role = parseUserRole(dto.role),
            isActive = dto.isActive,
            lastLogin = parseDateTime(dto.lastLogin),
            createdAt = parseDateTime(dto.createdAt) ?: Instant.ofEpochMilli(0L),
            updatedAt = parseDateTime(dto.updatedAt) ?: Instant.fromEpochMilliseconds(0),
            avatarUrl = dto.avatar
        )
    }

    fun mapToDto(domain: User): UserDto {
        return UserDto(
            id = domain.id,
            authId = domain.id, // Suponiendo que son iguales
            email = domain.email,
            username = domain.username,
            fullName = domain.fullName,
            role = domain.role.name,
            isActive = domain.isActive,
            lastLogin = domain.lastLogin?.toString(),
            createdAt = domain.createdAt.toString(),
            updatedAt = domain.updatedAt.toString(),
            avatar = domain.avatarUrl,
            accessToken = null // No disponible desde el dominio
        )
    }

    private fun parseUserRole(role: String?): UserRole {
        return try {
            if (role != null) UserRole.valueOf(role) else UserRole.RECEPTIONIST
        } catch (e: IllegalArgumentException) {
            UserRole.RECEPTIONIST
        }
    }

    private fun parseDateTime(dateTime: String?): Instant? {
        return try {
            dateTime?.let { Instant.parse(it) }
        } catch (e: Exception) {
            null
        }
    }
}