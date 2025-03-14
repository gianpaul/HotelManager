package exirium.pe.app.shared.feature.auth.data.source.remote

import exirium.pe.app.shared.core.network.SupabaseClientProvider
import exirium.pe.app.shared.core.network.error.NetworkException
import exirium.pe.app.shared.feature.auth.data.source.remote.dto.UserDto
import exirium.pe.app.shared.feature.auth.domain.model.UserRole
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

/**
 * Implementación de [AuthRemoteDataSource] utilizando Supabase.
 */
class SupabaseAuthDataSource(
    private val supabaseProvider: SupabaseClientProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRemoteDataSource {

    private val usersTable = "users"

    /**
     * Inicia sesión con email y contraseña.
     */
    override suspend fun signInWithEmail(email: String, password: String): Result<UserDto> =
        withContext(dispatcher) {
            try {
                // Autenticar con Auth de Supabase
                val response = supabaseProvider.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                // Obtener información del usuario autenticado
                val userInfo = supabaseProvider.auth.currentUserOrNull()
                    ?: return@withContext Result.failure(
                        NetworkException.AuthenticationError("Credenciales incorrectas")
                    )

                // Obtener o crear la entrada en la tabla users
                val userDto = getUserFromDatabase(userInfo.id) ?: createUserInDatabase(userInfo)

                // Añadir el token de acceso al DTO
                val userWithToken = userDto.copy(accessToken = userInfo.accessToken)

                Result.success(userWithToken)
            } catch (e: ClientRequestException) {
                Result.failure(mapExceptionToNetworkError(e))
            } catch (e: Exception) {
                Result.failure(
                    NetworkException.UnknownNetworkError(
                        message = e.message ?: "Error desconocido durante el inicio de sesión",
                        cause = e
                    )
                )
            }
        }

    /**
     * Registra un nuevo usuario con email y contraseña.
     */
    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        fullName: String,
        role: UserRole
    ): Result<UserDto> = withContext(dispatcher) {
        try {
            // Registrar con Auth de Supabase
            val response = supabaseProvider.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildMap {
                    put("full_name", fullName)
                    put("role", role.name)
                }
            }

            // Obtener información del usuario registrado
            val userInfo = supabaseProvider.auth.currentUserOrNull()
                ?: return@withContext Result.failure(
                    NetworkException.AuthenticationError("Error durante el registro")
                )

            // Crear entrada en la tabla users
            val userDto = createUserInDatabase(
                userInfo,
                fullName,
                role
            )

            // Añadir el token de acceso al DTO
            val userWithToken = userDto.copy(accessToken = userInfo.accessToken)

            Result.success(userWithToken)
        } catch (e: ClientRequestException) {
            when (e.response.status) {
                HttpStatusCode.Conflict -> Result.failure(
                    NetworkException.ConflictError("El email ya está registrado")
                )

                else -> Result.failure(mapExceptionToNetworkError(e))
            }
        } catch (e: Exception) {
            Result.failure(
                NetworkException.UnknownNetworkError(
                    message = e.message ?: "Error desconocido durante el registro",
                    cause = e
                )
            )
        }
    }

    /**
     * Cierra la sesión actual.
     */
    override suspend fun signOut(): Result<Unit> = withContext(dispatcher) {
        try {
            supabaseProvider.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(
                NetworkException.UnknownNetworkError(
                    message = e.message ?: "Error desconocido al cerrar sesión",
                    cause = e
                )
            )
        }
    }

    /**
     * Obtiene el usuario actualmente autenticado.
     */
    override suspend fun getCurrentUser(): UserDto? = withContext(dispatcher) {
        try {
            val userInfo = supabaseProvider.auth.currentUserOrNull() ?: return@withContext null
            val userDto = getUserFromDatabase(userInfo.id) ?: createUserInDatabase(userInfo)

            // Añadir el token de acceso al DTO
            userDto.copy(accessToken = userInfo.accessToken)
        } catch (e: Exception) {
            // En caso de error, simplemente retornamos null
            null
        }
    }

    /**
     * Refresca la sesión actual.
     */
    override suspend fun refreshSession(): Result<UserDto?> = withContext(dispatcher) {
        try {
            val response = supabaseProvider.auth.refreshCurrentSession()
            val userInfo = supabaseProvider.auth.currentUserOrNull()
                ?: return@withContext Result.success(null)

            val userDto = getUserFromDatabase(userInfo.id) ?: createUserInDatabase(userInfo)

            // Añadir el token de acceso al DTO
            val userWithToken = userDto.copy(accessToken = userInfo.accessToken)

            Result.success(userWithToken)
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                Result.success(null) // Token expirado, pero no es un error, simplemente no hay sesión
            } else {
                Result.failure(mapExceptionToNetworkError(e))
            }
        } catch (e: Exception) {
            Result.failure(
                NetworkException.UnknownNetworkError(
                    message = e.message ?: "Error desconocido al refrescar la sesión",
                    cause = e
                )
            )
        }
    }

    /**
     * Envía un enlace para restablecer la contraseña.
     */
    override suspend fun resetPassword(email: String): Result<Unit> = withContext(dispatcher) {
        try {
            supabaseProvider.auth.resetPasswordForEmail(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(
                NetworkException.UnknownNetworkError(
                    message = "Error al enviar el enlace de restablecimiento de contraseña",
                    cause = e
                )
            )
        }
    }

    /**
     * Guarda/actualiza el perfil de un usuario.
     */
    override suspend fun saveUserProfile(userDto: UserDto): Result<UserDto> =
        withContext(dispatcher) {
            try {
                // Primero verificamos si el usuario existe
                val existingUser = getUserFromDatabase(userDto.id)

                val result = if (existingUser != null) {
                    // Si existe, actualizar
                    updateUserInDatabase(userDto)
                } else {
                    // Si no existe, crear
                    insertUserInDatabase(userDto)
                }

                Result.success(result)
            } catch (e: Exception) {
                Result.failure(
                    NetworkException.UnknownNetworkError(
                        message = "Error al guardar el perfil del usuario",
                        cause = e
                    )
                )
            }
        }

    /**
     * Obtiene un usuario por su ID.
     */
    override suspend fun getUserById(id: String): Result<UserDto?> = withContext(dispatcher) {
        try {
            val user = getUserFromDatabase(id)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(
                NetworkException.UnknownNetworkError(
                    message = "Error al obtener el usuario",
                    cause = e
                )
            )
        }
    }

    // Funciones auxiliares privadas

    /**
     * Obtiene un usuario de la base de datos por su ID.
     */
    private suspend fun getUserFromDatabase(id: String): UserDto? {
        return try {
            val users = supabaseProvider.db.from(usersTable)
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeList<UserDto>()

            users.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Crea un nuevo usuario en la base de datos.
     */
    private suspend fun createUserInDatabase(
        userInfo: UserInfo,
        fullName: String? = null,
        role: UserRole? = null
    ): UserDto {
        val now = Clock.System.now()
        val username = userInfo.email?.substringBefore("@") ?: "user_${userInfo.id}"
        val userFullName = fullName
            ?: userInfo.userMetadata["full_name"] as? String
            ?: username
        val userRole = role?.name
            ?: userInfo.userMetadata["role"] as? String
            ?: UserRole.RECEPTIONIST.name

        val userData = UserDto(
            id = userInfo.id,
            authId = userInfo.id,
            email = userInfo.email ?: "",
            username = username,
            fullName = userFullName,
            role = userRole,
            isActive = true,
            lastLogin = now.toString(),
            createdAt = now.toString(),
            updatedAt = now.toString(),
            avatar = null,
            accessToken = null
        )

        return insertUserInDatabase(userData)
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     */
    private suspend fun insertUserInDatabase(userDto: UserDto): UserDto {
        return supabaseProvider.db.from(usersTable)
            .insert(userDto) {
                select()
            }
            .decodeAs()
    }

    /**
     * Actualiza un usuario existente en la base de datos.
     */
    private suspend fun updateUserInDatabase(userDto: UserDto): UserDto {
        return supabaseProvider.db.from(usersTable)
            .update({
                set("full_name", userDto.fullName)
                set("username", userDto.username)
                set("role", userDto.role)
                set("is_active", userDto.isActive)
                set("updated_at", Clock.System.now().toString())
                userDto.avatar?.let { set("avatar", it) }
            }) {
                filter {
                    eq("id", userDto.id)
                }
                select()
            }
            .decodeAs()
    }

    /**
     * Mapea excepciones HTTP a errores de red específicos.
     */
    private fun mapExceptionToNetworkError(e: ClientRequestException): NetworkException {
        return when (e.response.status) {
            HttpStatusCode.Unauthorized -> NetworkException.AuthenticationError("No autorizado")
            HttpStatusCode.Forbidden -> NetworkException.AuthorizationError("Acceso denegado")
            HttpStatusCode.NotFound -> NetworkException.NotFoundError("Recurso no encontrado")
            HttpStatusCode.Conflict -> NetworkException.ConflictError("Conflicto de recursos")
            HttpStatusCode.UnprocessableEntity -> NetworkException.ValidationError("Datos inválidos")
            HttpStatusCode.TooManyRequests -> NetworkException.RateLimitExceededError("Demasiadas solicitudes")
            HttpStatusCode.InternalServerError,
            HttpStatusCode.BadGateway,
            HttpStatusCode.ServiceUnavailable,
            HttpStatusCode.GatewayTimeout -> NetworkException.ServerError("Error del servidor")

            else -> NetworkException.UnknownNetworkError(
                message = "Error de red desconocido (${e.response.status.value})",
                cause = e
            )
        }
    }
}