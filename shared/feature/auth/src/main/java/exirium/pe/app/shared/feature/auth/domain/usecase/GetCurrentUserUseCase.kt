package exirium.pe.app.shared.feature.auth.domain.usecase

import exirium.pe.app.shared.feature.auth.domain.model.User
import exirium.pe.app.shared.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class GetCurrentUserUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }

    fun observe(): Flow<User?> {
        return authRepository.observeCurrentUser()
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}