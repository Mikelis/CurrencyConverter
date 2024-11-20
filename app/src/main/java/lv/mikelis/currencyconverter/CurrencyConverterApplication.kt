package lv.mikelis.currencyconverter

import android.app.Application
import kotlinx.coroutines.Dispatchers
import lv.mikelis.currencyconverter.networking.NetworkFactory
import lv.mikelis.currencyconverter.networking.repository.ApiRepository
import lv.mikelis.currencyconverter.viewmodel.ConverterViewModel

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import timber.log.Timber

class CurrencyConverterApplication : Application() {

    private val appModule = module {
        single(named("IODispatcher")) {
            Dispatchers.IO
        }
        single { NetworkFactory(get()).convertApi() }
        single { ApiRepository(get(named("IODispatcher")), get()) }
        viewModel { ConverterViewModel(get()) }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidLogger()
            androidContext(this@CurrencyConverterApplication)
            modules(appModule)
        }
    }
}