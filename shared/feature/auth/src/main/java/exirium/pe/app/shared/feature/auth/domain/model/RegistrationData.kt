package exirium.pe.app.shared.feature.auth.domain.model

data class RegistrationData(
    val email: String,
    val password: String,
    val fullName: String,
    val role: UserRole = UserRole.RECEPTIONIST
)