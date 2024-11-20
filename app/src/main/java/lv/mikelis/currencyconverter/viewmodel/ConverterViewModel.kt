package lv.mikelis.currencyconverter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lv.mikelis.currencyconverter.networking.repository.ApiRepository
import lv.mikelis.currencyconverter.networking.response.ConversionResponse

class ConverterViewModel(private val repository: ApiRepository) : ParentViewModel() {

    companion object {
        const val POLLED_INTERVAL = 3000L
        const val DEFAULT_CURRENCY = "EUR"
        const val DEFAULT_STARTING_VALUE = 1.0
    }

    init {
        getConversionRate()
    }

    private val _conversionRateFlow: MutableStateFlow<ConversionResponse?> = MutableStateFlow(null)
    val conversionRateFlow: StateFlow<ConversionResponse?> = _conversionRateFlow.asStateFlow()
    private var requestJob: Job? = null
    private var convertJob: Job? = null
    private var _currency = DEFAULT_CURRENCY
    private val _amount : MutableLiveData<Double> = MutableLiveData(DEFAULT_STARTING_VALUE)
    val amount get() = _amount


    fun getConversionRate(currency: String? = null) {
        if (currency != null) {
            _currency = currency
        }

        isLoading.value = true
        requestJob?.cancel()
        requestJob = viewModelScope.launch(exceptionHandler) {
            while (true) {
                val response = repository.getConversionRate(_currency)
                if (response != null) {
                    _conversionRateFlow.value = response
                }

                isLoading.value = false
                delay(POLLED_INTERVAL)
            }
        }
    }

    fun convertAmount(amount: Double, delayTime: Long = 200) {
        convertJob?.cancel()
        convertJob = viewModelScope.launch(exceptionHandler) {
            delay(delayTime)
            _amount.value = amount
        }
    }


    override fun onCleared() {
        super.onCleared()
        requestJob?.cancel()
    }
}