package exirium.pe.app.shared.feature.auth.domain.model

data class AuthResult(
    val user: User,
    val token: String,
    val refreshToken: String? = null,
    val isNewUser: Boolean = false
)