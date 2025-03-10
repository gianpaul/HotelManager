package exirium.pe.app.shared.feature.auth.mapper

class HotelMapper {
    fun mapToUi(domain: DomainHotel): UiHotel {
        return UiHotel(
            id = domain.id,
            name = domain.name
        )
    }
}