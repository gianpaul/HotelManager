package exirium.pe.app.shared.domain.model

import java.time.Instant

enum class UserRole {
    ADMIN,
    RECEPTION,
    CLEANING
}

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val hotelId: String,
    val isActive: Boolean = true,
    val lastLogin: Instant? = null,
    val token: String = "",
    val profileImageUrl: String? = null
)