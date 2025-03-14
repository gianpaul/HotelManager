package exirium.pe.app.shared.domain.model

import java.time.Instant

data class Session(
    val accessToken: String,
    val refreshToken: String?,
    val expiresAt: Instant,
)
