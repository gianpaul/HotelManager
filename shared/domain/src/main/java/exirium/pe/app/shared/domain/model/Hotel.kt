package exirium.pe.app.shared.domain.model

data class Hotel(
    val id: String,
    val name: String,
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val isActive: Boolean = true,
    val configuration: HotelConfiguration = HotelConfiguration()
)

data class HotelConfiguration(
    val primaryColor: String = "#4285F4",
    val logoUrl: String? = null,
    val currencySymbol: String = "$",
    val taxPercentage: Float = 0.18f,
    val checkInTime: String = "14:00",
    val checkOutTime: String = "12:00",
    val features: List<String> = emptyList()
)