package exirium.pe.app.shared.feature.auth.domain.model

import exirium.pe.app.shared.domain.model.UserRole

data class Registration(
    val email: String,
    val password: String,
    val fullName: String,
    val role: UserRole = UserRole.RECEPTIONIST
)