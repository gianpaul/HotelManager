package exirium.pe.app.shared.feature.auth.data.repository

import exirium.pe.app.shared.feature.auth.data.mapper.UserMapper
import exirium.pe.app.shared.feature.auth.data.source.local.AuthLocalDataSource
import exirium.pe.app.shared.feature.auth.data.source.remote.AuthRemoteDataSource
import exirium.pe.app.shared.feature.auth.domain.model.AuthResult
import exirium.pe.app.shared.feature.auth.domain.model.Credentials
import exirium.pe.app.shared.feature.auth.domain.model.RegistrationData
import exirium.pe.app.shared.feature.auth.domain.model.User
import exirium.pe.app.shared.feature.auth.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource,
    private val userMapper: UserMapper,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) : AuthRepository {

    /**
     * Inicia sesión con credenciales de usuario.
     */
    override suspend fun signIn(credentials: Credentials): Result<AuthResult> = withContext(dispatcher) {
        remoteDataSource.signInWithEmail(
            email = credentials.email,
            password = credentials.password
        ).map { userDto ->
            // Mapear DTO a modelo de dominio
            val user = userMapper.mapToDomain(userDto)

            // Guardar el usuario en caché local
            localDataSource.saveCurrentUser(userDto)

            // Crear y devolver el resultado de autenticación
            AuthResult(
                user = user,
                token = userDto.accessToken ?: ""
            )
        }
    }

    /**
     * Registra un nuevo usuario.
     */
    override suspend fun signUp(data: RegistrationData): Result<AuthResult> = withContext(dispatcher) {
        remoteDataSource.signUpWithEmail(
            email = data.email,
            password = data.password,
            fullName = data.fullName,
            role = data.role
        ).map { userDto ->
            // Mapear DTO a modelo de dominio
            val user = userMapper.mapToDomain(userDto)

            // Guardar el usuario en caché local
            localDataSource.saveCurrentUser(userDto)

            // Crear y devolver el resultado de autenticación
            AuthResult(
                user = user,
                token = userDto.accessToken ?: ""
            )
        }
    }

    /**
     * Cierra la sesión del usuario.
     */
    override suspend fun signOut(): Result<Unit> = withContext(dispatcher) {
        // Cerrar sesión en la fuente remota
        val result = remoteDataSource.signOut()

        // Independientemente del resultado, limpiamos la caché local
        localDataSource.clearCurrentUser()

        result
    }

    /**
     * Comprueba si hay un usuario autenticado.
     */
    override fun isLoggedIn(): Boolean {
        // Primero verificamos si hay un usuario en caché
        val cachedUser = localDataSource.getCurrentUserSync()

        return cachedUser != null
    }

    /**
     * Obtiene el usuario actual autenticado.
     */
    override suspend fun getCurrentUser(): User? = withContext(dispatcher) {
        // Primero intentamos obtener de caché
        val cachedUserDto = localDataSource.getCurrentUser()

        if (cachedUserDto != null) {
            // Si hay usuario en caché, lo devolvemos
            userMapper.mapToDomain(cachedUserDto)
        } else {
            // Si no hay en caché, intentamos obtener del servidor
            val remoteUserDto = remoteDataSource.getCurrentUser()

            // Si obtenemos el usuario del servidor, lo guardamos en caché
            if (remoteUserDto != null) {
                localDataSource.saveCurrentUser(remoteUserDto)
                userMapper.mapToDomain(remoteUserDto)
            } else {
                null
            }
        }
    }

    /**
     * Observa cambios en el usuario actual.
     */
    override fun observeCurrentUser(): Flow<User?> {
        return localDataSource.observeCurrentUser()
            .map { userDto ->
                userDto?.let { userMapper.mapToDomain(it) }
            }
    }

    /**
     * Obtiene un usuario por su ID.
     */
    override suspend fun getUserById(id: String): Result<User?> = withContext(dispatcher) {
        remoteDataSource.getUserById(id).map { userDto ->
            userDto?.let { userMapper.mapToDomain(it) }
        }
    }

    /**
     * Envía un enlace para restablecer la contraseña.
     */
    override suspend fun resetPassword(email: String): Result<Unit> = withContext(dispatcher) {
        remoteDataSource.resetPassword(email)
    }

    /**
     * Actualiza el perfil de un usuario.
     */
    override suspend fun updateUserProfile(user: User): Result<User> = withContext(dispatcher) {
        // Convertir de dominio a DTO
        val userDto = userMapper.mapToDto(user)

        remoteDataSource.saveUserProfile(userDto).map { updatedUserDto ->
            // Convertir DTO actualizado a dominio
            val updatedUser = userMapper.mapToDomain(updatedUserDto)

            // Si es el usuario actual, actualizar en caché
            val currentUser = localDataSource.getCurrentUser()
            if (currentUser?.id == updatedUserDto.id) {
                localDataSource.saveCurrentUser(updatedUserDto)
            }

            updatedUser
        }
    }
}