package lv.mikelis.currencyconverter.networking.repository


import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import lv.mikelis.currencyconverter.networking.api.CurrencyApiService
import lv.mikelis.currencyconverter.networking.response.ConversionResponse
import retrofit2.Response
import timber.log.Timber
import kotlin.toString

class ApiRepository(
    private val ioDispatcher: CoroutineDispatcher,
    private val service: CurrencyApiService
) {


    suspend fun getConversionRate(currency: String = "EUR"): ConversionResponse? {
        return withContext(ioDispatcher) {
            try {
                val response =
                    service.getConversionRate(currency)
                return@withContext handleResponse(response)
            } catch (e: Exception) {
                Timber.e(e.toString())
                throw e
            }
        }
    }


    private fun <T> handleResponse(response: Response<T>): T? {
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Throwable(
                message = response.errorBody()?.string()
            )
        }
    }
}