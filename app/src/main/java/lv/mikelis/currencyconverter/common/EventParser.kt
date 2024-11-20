package lv.mikelis.currencyconverter.common

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.collectLatest
import lv.mikelis.currencyconverter.viewmodel.ParentViewModel
import timber.log.Timber
import kotlin.collections.forEach

@Composable
fun EventParser(vararg viewModel: ParentViewModel) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.forEach { vm ->
            vm.errorEvent.collectLatest {
                Timber.e(it.localizedMessage)
                Toast.makeText(
                    context,
                    ("Error " + it.localizedMessage), Toast.LENGTH_LONG
                ).show()

            }
        }
    }

}
