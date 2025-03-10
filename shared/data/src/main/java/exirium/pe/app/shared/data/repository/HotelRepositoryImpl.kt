package exirium.pe.app.shared.data.repository

import exirium.pe.app.shared.domain.model.Hotel
import exirium.pe.app.shared.domain.repository.HotelRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HotelRepositoryImpl(
    private val postgrest: Postgrest,
    private val mapper: HotelMapper,
    private val tokenStorage: TokenStorage,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : HotelRepository {

    override suspend fun getAvailableHotels(): Result<List<Hotel>> = withContext(ioDispatcher) {
        try {
            val hotelsDto = postgrest["hotels"]
                .select { filter { eq("is_active", true) } }
                .decodeList<HotelDto>()

            val hotels = hotelsDto.map { mapper.mapToDomain(it) }
            Result.success(hotels)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getHotelById(id: String): Result<Hotel> = withContext(ioDispatcher) {
        try {
            val hotelDto = postgrest["hotels"]
                .select { filter { eq("id", id) } }
                .decodeSingle<HotelDto>()

            Result.success(mapper.mapToDomain(hotelDto))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}