package exirium.pe.app.shared.domain.validator

class LoginValidator {
    fun validateCredentials(email: String, password: String, hotelId: String?): Result<Unit> {
        val errors = mutableListOf<String>()

        if (email.isBlank()) {
            errors.add("El correo electrónico no puede estar vacío")
        } else if (!email.matches(EMAIL_REGEX)) {
            errors.add("Formato de correo electrónico inválido")
        }

        if (password.isBlank()) {
            errors.add("La contraseña no puede estar vacía")
        } else if (password.length < 6) {
            errors.add("La contraseña debe tener al menos 6 caracteres")
        }

        if (hotelId.isNullOrBlank()) {
            errors.add("Debe seleccionar un hotel")
        }

        return if (errors.isEmpty()) {
            Result.success(Unit)
        } else {
            Result.failure(ValidationException(errors.joinToString("; ")))
        }
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}

class ValidationException(message: String) : Exception(message)