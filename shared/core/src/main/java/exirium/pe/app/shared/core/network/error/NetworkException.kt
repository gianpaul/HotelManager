package exirium.pe.app.shared.core.network.error

sealed class NetworkException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    /**
     * Error de autenticación (credenciales inválidas, token expirado, etc.)
     */
    class AuthenticationError(
        override val message: String = "Error de autenticación",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    /**
     * Error de autorización (permisos insuficientes)
     */
    class AuthorizationError(
        override val message: String = "No tienes permisos para realizar esta acción",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    /**
     * Error de conexión (sin internet, timeout, etc.)
     */
    class ConnectionError(
        override val message: String = "Error de conexión",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    /**
     * Error de servidor (500, 502, 503, etc.)
     */
    class ServerError(
        override val message: String = "Error del servidor",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    /**
     * Recurso no encontrado (404)
     */
    class NotFoundError(
        override val message: String = "Recurso no encontrado",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    /**
     * Error de validación (datos inválidos)
     */
    class ValidationError(
        override val message: String = "Datos inválidos",
        override val cause: Throwable? = null,
        val fieldErrors: Map<String, String> = emptyMap()
    ) : NetworkException(message, cause)

    /**
     * Error de conflicto (por ejemplo, email ya registrado)
     */
    class ConflictError(
        override val message: String = "Conflicto de recursos",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    /**
     * Error por límite de peticiones excedido
     */
    class RateLimitExceededError(
        override val message: String = "Has excedido el límite de peticiones",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)

    /**
     * Error de red desconocido
     */
    class UnknownNetworkError(
        override val message: String = "Error de red desconocido",
        override val cause: Throwable? = null
    ) : NetworkException(message, cause)
}