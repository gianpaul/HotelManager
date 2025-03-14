package exirium.pe.app.shared.domain.error

abstract class DomainError(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {
    abstract val code: String
}