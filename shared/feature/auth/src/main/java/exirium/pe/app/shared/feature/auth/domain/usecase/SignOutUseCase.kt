package exirium.pe.app.shared.feature.auth.domain.usecase

import exirium.pe.app.shared.feature.auth.domain.repository.AuthRepository

class SignOutUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(): Result<Unit> {
        return authRepository.signOut()
    }
}