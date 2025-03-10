package exirium.pe.app.shared.domain.auth

import exirium.pe.app.shared.domain.model.Hotel
import exirium.pe.app.shared.domain.model.User
import exirium.pe.app.shared.domain.repository.AuthRepository
import exirium.pe.app.shared.domain.repository.HotelRepository
import exirium.pe.app.shared.domain.validator.LoginValidator

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val hotelRepository: HotelRepository,
    private val validator: LoginValidator
) {
    suspend fun login(email: String, password: String, hotelId: String): Result<User> {
        val validationResult = validator.validateCredentials(email, password, hotelId)

        if (validationResult.isFailure) {
            return validationResult.map { null as User }
        }

        return authRepository.login(email, password, hotelId)
    }

    suspend fun getAvailableHotels(): Result<List<Hotel>> {
        return hotelRepository.getAvailableHotels()
    }
}