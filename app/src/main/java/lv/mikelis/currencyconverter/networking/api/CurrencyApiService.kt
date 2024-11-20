package lv.mikelis.currencyconverter.networking.api


import lv.mikelis.currencyconverter.networking.response.ConversionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface CurrencyApiService {
    @GET("latest")
    suspend fun getConversionRate(
        @Query("base") currency: String
    ): Response<ConversionResponse>

}