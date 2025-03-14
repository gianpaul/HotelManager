package exirium.pe.app.shared.feature.auth.domain.model

import java.time.Instant

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val role: UserRole,
    val isActive: Boolean = true,
    val lastLogin: Instant? = null,
    val createdAt: Instant
)