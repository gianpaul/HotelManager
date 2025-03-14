package exirium.pe.app.shared.feature.auth.domain.model

enum class UserRole {
    ADMIN,          // Administrador - acceso total al sistema
    MANAGER,        // Gerente - acceso a reportes y algunas configuraciones
    RECEPTIONIST,   // Recepcionista - gestión diaria de hotel
    CLEANER         // Limpieza - acceso limitado a gestión de habitaciones
}
