package exirium.pe.app.shared.domain.repository

import exirium.pe.app.shared.domain.model.Hotel

interface HotelRepository {
    suspend fun getAvailableHotels(): Result<List<Hotel>>
    suspend fun getHotelById(id: String): Result<Hotel>
}