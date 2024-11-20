package lv.mikelis.currencyconverter.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

abstract class ParentViewModel() : ViewModel() {

    protected val _errorEvent: MutableSharedFlow<Throwable> = MutableSharedFlow()
    val errorEvent = _errorEvent.asSharedFlow()
    var isLoading = MutableLiveData<Boolean>(false)


    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        viewModelScope.launch {
            _errorEvent.emit(exception)
            isLoading.value = false
        }
    }
}