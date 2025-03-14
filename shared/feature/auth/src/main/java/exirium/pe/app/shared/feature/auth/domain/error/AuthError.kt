package exirium.pe.app.shared.feature.auth.domain.error

import exirium.pe.app.shared.domain.error.DomainError

sealed class AuthError(
    override val message: String,
    override val cause: Throwable? = null,
    override val code: String
) : DomainError(message, cause) {
    class InvalidCredentials(
        override val cause: Throwable? = null
    ) : AuthError(
        message = "Email o contraseña incorrectos",
        cause = cause,
        code = "AUTH_001"
    )

    class EmailAlreadyExists(
        override val cause: Throwable? = null
    ) : AuthError(
        message = "El email ya está registrado",
        cause = cause,
        code = "AUTH_002"
    )

    class SessionExpired(
        override val cause: Throwable? = null
    ) : AuthError(
        message = "La sesión ha expirado",
        cause = cause,
        code = "AUTH_003"
    )

    class Unauthorized(
        override val cause: Throwable? = null
    ) : AuthError(
        message = "No está autenticado",
        cause = cause,
        code = "AUTH_004"
    )

    class Forbidden(
        override val cause: Throwable? = null
    ) : AuthError(
        message = "No tiene permisos para realizar esta acción",
        cause = cause,
        code = "AUTH_005"
    )


    class WeakPassword(
        override val cause: Throwable? = null
    ) : AuthError(
        message = "La contraseña es demasiado débil",
        cause = cause,
        code = "AUTH_006"
    )

    class PasswordResetFailed(
        override val cause: Throwable? = null
    ) : AuthError(
        message = "No se pudo enviar el enlace de restablecimiento",
        cause = cause,
        code = "AUTH_007"
    )

    class Unknown(
        override val message: String = "Error de autenticación desconocido",
        override val cause: Throwable? = null
    ) : AuthError(
        message = message,
        cause = cause,
        code = "AUTH_999"
    )
}