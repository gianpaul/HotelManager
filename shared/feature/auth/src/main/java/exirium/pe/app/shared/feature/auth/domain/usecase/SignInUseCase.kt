package exirium.pe.app.shared.feature.auth.domain.usecase

import exirium.pe.app.shared.feature.auth.domain.model.AuthResult
import exirium.pe.app.shared.feature.auth.domain.model.Credentials
import exirium.pe.app.shared.feature.auth.domain.repository.AuthRepository

class SignInUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Result<AuthResult> {
        val credentials = Credentials(email, password)
        return authRepository.signIn(credentials)
    }

    suspend operator fun invoke(credentials: Credentials): Result<AuthResult> {
        return authRepository.signIn(credentials)
    }
}