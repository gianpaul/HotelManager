package exirium.pe.app.shared.feature.auth.data.source.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    /**
     * ID único del usuario
     */
    val id: String,

    /**
     * ID de autenticación - normalmente igual al ID en Supabase
     */
    @SerialName("auth_id")
    val authId: String,

    /**
     * Correo electrónico del usuario
     */
    val email: String,

    /**
     * Nombre de usuario (suele ser derivado del email)
     */
    val username: String,

    /**
     * Nombre completo del usuario
     */
    @SerialName("full_name")
    val fullName: String,

    /**
     * Rol del usuario en la aplicación
     */
    val role: String,

    /**
     * Indica si el usuario está activo
     */
    @SerialName("is_active")
    val isActive: Boolean = true,

    /**
     * Fecha/hora del último inicio de sesión (formato ISO)
     */
    @SerialName("last_login")
    val lastLogin: String? = null,

    /**
     * Fecha/hora de creación (formato ISO)
     */
    @SerialName("created_at")
    val createdAt: String,

    /**
     * Fecha/hora de última actualización (formato ISO)
     */
    @SerialName("updated_at")
    val updatedAt: String,

    /**
     * URL del avatar del usuario
     */
    val avatar: String? = null,

    /**
     * Token de acceso (puede ser null en algunas operaciones)
     * Este campo no se guarda en la base de datos, solo se usa para la sesión
     */
    @Serializable(with = NullableStringSerializer::class)
    @SerialName("access_token")
    val accessToken: String? = null
)

/**
 * Serializador personalizado para campos String nullable
 * Útil para manejar casos donde el campo puede no existir en el JSON
 */
@Serializable
object NullableStringSerializer : kotlinx.serialization.KSerializer<String?> {
    override val descriptor: kotlinx.serialization.descriptors.SerialDescriptor =
        kotlinx.serialization.descriptors.PrimitiveSerialDescriptor("NullableString", kotlinx.serialization.descriptors.PrimitiveKind.STRING)

    override fun serialize(encoder: kotlinx.serialization.encoding.Encoder, value: String?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            encoder.encodeString(value)
        }
    }

    override fun deserialize(decoder: kotlinx.serialization.encoding.Decoder): String? {
        return try {
            decoder.decodeString()
        } catch (e: Exception) {
            null
        }
    }
}