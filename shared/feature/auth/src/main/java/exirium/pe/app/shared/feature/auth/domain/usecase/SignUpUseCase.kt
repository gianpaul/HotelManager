package exirium.pe.app.shared.feature.auth.domain.usecase

import exirium.pe.app.shared.feature.auth.domain.model.AuthResult
import exirium.pe.app.shared.feature.auth.domain.model.RegistrationData
import exirium.pe.app.shared.feature.auth.domain.model.UserRole
import exirium.pe.app.shared.feature.auth.domain.repository.AuthRepository

class SignUpUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(
        email: String,
        password: String,
        fullName: String,
        role: UserRole = UserRole.RECEPTIONIST
    ): Result<AuthResult> {
        val registrationData = RegistrationData(email, password, fullName, role)
        return authRepository.signUp(registrationData)
    }

    suspend operator fun invoke(data: RegistrationData): Result<AuthResult> {
        return authRepository.signUp(data)
    }
}