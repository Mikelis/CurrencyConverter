package lv.mikelis.currencyconverter

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import lv.mikelis.currencyconverter.networking.api.CurrencyApiService
import lv.mikelis.currencyconverter.networking.repository.ApiRepository
import lv.mikelis.currencyconverter.networking.response.ConversionResponse
import lv.mikelis.currencyconverter.viewmodel.ConverterViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: ConverterViewModel
    private lateinit var repository: ApiRepository

    @Before
    fun setUp() {
        val mockApi = mockk<CurrencyApiService> {
            coEvery { getConversionRate("USD") } returns Response.success(
                ConversionResponse(
                    base = "USD",
                    date = "",
                    amount = 1.0,
                    rates = HashMap<String, Double>()
                )
            )
        }
        repository = ApiRepository(Dispatchers.IO, mockApi)
        viewModel = ConverterViewModel(repository)
    }

    @Test
    fun checkIfRequestCreatedCorrectly() = runTest {
        val response = repository.getConversionRate("USD")
        assertEquals("USD", response?.base)
    }

    @Test
    fun checkIfVmDataChanges() = runTest {
        val expectedBase = "USD"
        viewModel.getConversionRate(expectedBase)
        val result = viewModel.conversionRateFlow.first()
        assertEquals(expectedBase, result?.base)
    }

    @Test
    fun checkIfVmLoadingFalse() = runTest {
        viewModel.getConversionRate("USD")
        viewModel.conversionRateFlow.first()
        assertEquals(false, viewModel.isLoading.value)
    }

    @Test
    fun checkIfVmLoadingFalseIfException() = runTest {
        val mockApi = mockk<CurrencyApiService> {
            coEvery { getConversionRate("USD") } returns Response.error(400,
                ResponseBody.create(
                    "application/json".toMediaTypeOrNull(),
                    "{\"error\":\"Invalid base currency\"}"
                )
            )
        }
        val repository = ApiRepository(Dispatchers.IO, mockApi)
        val viewModel = ConverterViewModel(repository)
        viewModel.getConversionRate("USD")
        viewModel.conversionRateFlow.first()
        assertEquals(false, viewModel.isLoading.value)
    }
}
