package exirium.pe.app.shared.feature.auth.contract

sealed class LoginState : MviState {
    object Initial : LoginState()
    object Loading : LoginState()
    data class Error(val message: String) : LoginState()
    data class ValidationError(
        val emailError: String? = null,
        val passwordError: String? = null,
        val hotelError: String? = null
    ) : LoginState()
    data class Authenticated(val userId: String) : LoginState()

    data class InputFields(
        val email: String = "",
        val password: String = "",
        val selectedHotelId: String? = null,
        val isLoading: Boolean = false,
        val hotels: List<Hotel> = emptyList()
    ) : LoginState()
}

sealed class LoginIntent : MviIntent {
    data class UpdateEmail(val email: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    data class SelectHotel(val hotelId: String) : LoginIntent()
    object SubmitLogin : LoginIntent()
    object LoadHotels : LoginIntent()
    object ClearErrors : LoginIntent()
}

sealed class LoginEffect : MviEffect {
    data class ShowToast(val message: String) : LoginEffect()
    object NavigateToDashboard : LoginEffect()
    data class SetToken(val token: String) : LoginEffect()
}

data class Hotel(
    val id: String,
    val name: String
)