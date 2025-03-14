package exirium.pe.app.shared.feature.auth.presentation.viewmodel

import exirium.pe.app.shared.core.presentation.mvi.implementation.BaseViewModel
import exirium.pe.app.shared.domain.model.Session
import exirium.pe.app.shared.feature.auth.application.usecase.GetSessionUseCase
import exirium.pe.app.shared.feature.auth.application.usecase.ResetPasswordUseCase
import exirium.pe.app.shared.feature.auth.application.usecase.SignInUseCase
import exirium.pe.app.shared.feature.auth.application.usecase.SignOutUseCase
import exirium.pe.app.shared.feature.auth.application.usecase.SignUpUseCase
import exirium.pe.app.shared.feature.auth.domain.model.Credentials
import exirium.pe.app.shared.feature.auth.domain.model.Registration
import exirium.pe.app.shared.feature.auth.presentation.mvi.AuthEffect
import exirium.pe.app.shared.feature.auth.presentation.mvi.AuthIntent
import exirium.pe.app.shared.feature.auth.presentation.mvi.AuthProcessor
import exirium.pe.app.shared.feature.auth.presentation.mvi.AuthReducer
import exirium.pe.app.shared.feature.auth.presentation.mvi.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    signInUseCase: SignInUseCase,
    signUpUseCase: SignUpUseCase,
    signOutUseCase: SignOutUseCase,
    getSessionUseCase: GetSessionUseCase,
    resetPasswordUseCase: ResetPasswordUseCase
) : BaseViewModel<AuthIntent, AuthState, AuthEffect>(
    initialState = AuthState(),
    reducer = AuthReducer(),
    processor = AuthProcessor(
        signInUseCase,
        signUpUseCase,
        signOutUseCase,
        getSessionUseCase,
        resetPasswordUseCase
    )
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Estado UI para controlar campos y validación en pantallas de inicio de sesión/registro
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        // Comprobar el estado de autenticación al iniciar
        checkAuthState()

        // Observar el flujo de sesión para mantener el estado actualizado
        viewModelScope.launch {
            getSessionUseCase().collect { session ->
                val isAuthenticated = session != null
                updateAuthState(session, isAuthenticated)
            }
        }
    }

    /**
     * Actualiza el estado de autenticación.
     */
    private fun updateAuthState(session: Session?, isAuthenticated: Boolean) {
        val currentState = state.value

        if (currentState.isAuthenticated != isAuthenticated || currentState.session != session) {
            val newState = currentState.copy(
                isAuthenticated = isAuthenticated,
                session = session,
                isLoading = false,
                error = null
            )

            // Actualizar el estado MVI
            (store as MutableStateFlow<AuthState>).value = newState
        }
    }

    /**
     * Comprueba el estado de autenticación actual.
     */
    fun checkAuthState() {
        dispatch(AuthIntent.CheckAuthState)
    }

    /**
     * Inicia sesión con email y contraseña.
     */
    fun signIn(email: String, password: String) {
        if (validateSignInForm(email, password)) {
            val credentials = Credentials(email, password)
            dispatch(AuthIntent.SignIn(credentials))
        }
    }

    /**
     * Registra un nuevo usuario.
     */
    fun signUp(email: String, password: String, confirmPassword: String, fullName: String, role: UserRole = UserRole.RECEPTIONIST) {
        if (validateSignUpForm(email, password, confirmPassword, fullName)) {
            val registration = Registration(email, password, fullName, role)
            dispatch(AuthIntent.SignUp(registration))
        }
    }

    /**
     * Cierra la sesión actual.
     */
    fun signOut() {
        dispatch(AuthIntent.SignOut)
    }

    /**
     * Solicita un restablecimiento de contraseña.
     */
    fun resetPassword(email: String) {
        if (validateEmail(email)) {
            dispatch(AuthIntent.ResetPassword(email))
        }
    }

    /**
     * Valida el formulario de inicio de sesión.
     */
    private fun validateSignInForm(email: String, password: String): Boolean {
        var isValid = true
        val errors = mutableMapOf<String, String>()

        if (!validateEmail(email)) {
            isValid = false
            errors["email"] = "Email inválido"
        }

        if (password.isBlank()) {
            isValid = false
            errors["password"] = "Contraseña requerida"
        }

        _uiState.update { it.copy(formErrors = errors) }
        return isValid
    }

    /**
     * Valida el formulario de registro.
     */
    private fun validateSignUpForm(email: String, password: String, confirmPassword: String, fullName: String): Boolean {
        var isValid = true
        val errors = mutableMapOf<String, String>()

        if (!validateEmail(email)) {
            isValid = false
            errors["email"] = "Email inválido"
        }

        if (fullName.isBlank()) {
            isValid = false
            errors["fullName"] = "Nombre completo requerido"
        }

        if (password.length < 8) {
            isValid = false
            errors["password"] = "La contraseña debe tener al menos 8 caracteres"
        }

        if (password != confirmPassword) {
            isValid = false
            errors["confirmPassword"] = "Las contraseñas no coinciden"
        }

        _uiState.update { it.copy(formErrors = errors) }
        return isValid
    }

    /**
     * Valida un email.
     */
    private fun validateEmail(email: String): Boolean {
        return email.contains('@') && email.contains('.')
    }

    /**
     * Actualiza un campo del formulario.
     */
    fun updateField(field: String, value: String) {
        val currentErrors = _uiState.value.formErrors.toMutableMap()
        currentErrors.remove(field) // Limpiar error al editar el campo

        _uiState.update {
            when (field) {
                "email" -> it.copy(email = value, formErrors = currentErrors)
                "password" -> it.copy(password = value, formErrors = currentErrors)
                "confirmPassword" -> it.copy(confirmPassword = value, formErrors = currentErrors)
                "fullName" -> it.copy(fullName = value, formErrors = currentErrors)
                else -> it
            }
        }
    }

    /**
     * Limpia los errores del formulario.
     */
    fun clearFormErrors() {
        _uiState.update { it.copy(formErrors = emptyMap()) }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val fullName: String = "",
    val formErrors: Map<String, String> = emptyMap()
)