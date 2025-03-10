package exirium.pe.app.shared.feature.auth.store

class LoginStore(
    private val loginUseCase: LoginUseCase,
    private val hotelMapper: HotelMapper,
    coroutineScope: CoroutineScope
) : BaseMviStore<LoginState, LoginIntent, LoginEffect>(
    initialState = LoginState.InputFields(),
    coroutineScope = coroutineScope
) {

    init {
        loadHotels()
    }

    override suspend fun processIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UpdateEmail -> updateEmail(intent.email)
            is LoginIntent.UpdatePassword -> updatePassword(intent.password)
            is LoginIntent.SelectHotel -> selectHotel(intent.hotelId)
            LoginIntent.SubmitLogin -> submitLogin()
            LoginIntent.LoadHotels -> loadHotels()
            LoginIntent.ClearErrors -> clearErrors()
        }
    }

    private fun updateEmail(email: String) {
        updateState {
            when (this) {
                is LoginState.InputFields -> copy(email = email)
                else -> LoginState.InputFields(email = email)
            }
        }
    }

    private fun updatePassword(password: String) {
        updateState {
            when (this) {
                is LoginState.InputFields -> copy(password = password)
                else -> LoginState.InputFields(password = password)
            }
        }
    }

    private fun selectHotel(hotelId: String) {
        updateState {
            when (this) {
                is LoginState.InputFields -> copy(selectedHotelId = hotelId)
                else -> LoginState.InputFields(selectedHotelId = hotelId)
            }
        }
    }

    private fun clearErrors() {
        updateState {
            when (this) {
                is LoginState.ValidationError -> LoginState.InputFields()
                is LoginState.Error -> LoginState.InputFields()
                else -> this
            }
        }
    }

    private fun submitLogin() {
        val currentState = states.value
        if (currentState !is LoginState.InputFields) return

        val email = currentState.email
        val password = currentState.password
        val hotelId = currentState.selectedHotelId

        if (hotelId == null) {
            updateState {
                LoginState.ValidationError(hotelError = "Debe seleccionar un hotel")
            }
            return
        }

        updateState {
            when (this) {
                is LoginState.InputFields -> copy(isLoading = true)
                else -> LoginState.Loading
            }
        }

        coroutineScope.launch {
            loginUseCase.login(email, password, hotelId)
                .onSuccess { user ->
                    updateState { LoginState.Authenticated(user.id) }
                    emitEffect(LoginEffect.SetToken(user.token))
                    emitEffect(LoginEffect.NavigateToDashboard)
                }
                .onFailure { error ->
                    when (error) {
                        is ValidationException -> {
                            updateState {
                                LoginState.ValidationError(
                                    emailError = if (email.isBlank() || !email.contains("@")) "Email inválido" else null,
                                    passwordError = if (password.length < 6) "Contraseña debe tener al menos 6 caracteres" else null,
                                    hotelError = if (hotelId.isBlank()) "Seleccione un hotel" else null
                                )
                            }
                        }
                        else -> {
                            updateState { LoginState.Error(error.message ?: "Error desconocido") }
                            emitEffect(LoginEffect.ShowToast("Error al iniciar sesión: ${error.message}"))
                        }
                    }
                }
        }
    }

    private fun loadHotels() {
        coroutineScope.launch {
            loginUseCase.getAvailableHotels()
                .onSuccess { domainHotels ->
                    val hotels = domainHotels.map { hotelMapper.mapToUi(it) }
                    updateState {
                        when (this) {
                            is LoginState.InputFields -> copy(hotels = hotels)
                            else -> LoginState.InputFields(hotels = hotels)
                        }
                    }
                }
                .onFailure { error ->
                    emitEffect(LoginEffect.ShowToast("Error al cargar hoteles: ${error.message}"))
                }
        }
    }
}