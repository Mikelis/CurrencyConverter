package lv.mikelis.currencyconverter

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import lv.mikelis.currencyconverter.networking.NetworkFactory
import lv.mikelis.currencyconverter.networking.repository.ApiRepository
import lv.mikelis.currencyconverter.viewmodel.ConverterViewModel
import org.junit.Before
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class ConverterViewModelTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainDispatcherRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: ConverterViewModel
    private lateinit var repository: ApiRepository

    @Before
    fun setUp() {
        repository = ApiRepository(Dispatchers.IO, NetworkFactory(mockk<Context>(relaxed = true), false).convertApi())
        viewModel = ConverterViewModel(repository)
    }


}
