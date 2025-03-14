package exirium.pe.app.shared.feature.auth.domain.usecase

import exirium.pe.app.shared.feature.auth.domain.repository.AuthRepository

class ResetPasswordUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String): Result<Unit> {
        return authRepository.resetPassword(email)
    }
}