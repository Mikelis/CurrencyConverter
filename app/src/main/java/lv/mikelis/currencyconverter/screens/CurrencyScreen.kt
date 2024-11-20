import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import lv.mikelis.currencyconverter.common.EventParser
import lv.mikelis.currencyconverter.common.toCurrency
import lv.mikelis.currencyconverter.common.toCurrencyFullName
import lv.mikelis.currencyconverter.ui.theme.SecondaryBlue
import lv.mikelis.currencyconverter.viewmodel.ConverterViewModel
import org.koin.androidx.compose.koinViewModel
import timber.log.Timber
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.toUpperCase
import lv.mikelis.currencyconverter.common.toCurrencyWithoutSymbol
import lv.mikelis.currencyconverter.viewmodel.ConverterViewModel.Companion.DEFAULT_STARTING_VALUE
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrencyScreen(
    modifier: Modifier = Modifier,
    viewModel: ConverterViewModel = koinViewModel()
) {
    val currencyRates by viewModel.conversionRateFlow.collectAsState()
    val amount by viewModel.amount.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState()

    var topCard = Pair(
        currencyRates?.base ?: "",
        amount ?: currencyRates?.amount ?: 0.0
    )

    val sortedItems by remember(currencyRates) {
        derivedStateOf {
            currencyRates?.rates?.toSortedMap() ?: mapOf<String, Double>()
        }
    }

    EventParser(viewModel)

    Box {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {


            item {
                val backgroundColor = SecondaryBlue
                val contentColor = Color.White

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = backgroundColor,
                        contentColor = contentColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement(tween(500))
                        .clickable {

                        }
                ) {
                    SingleTopItem(topCard, contentColor, viewModel)
                }
            }

            itemsIndexed(
                items = sortedItems.toList(),
                key = { _, item -> item.first }
            ) { index, item ->


                val backgroundColor = Color(0xFFE6E6EB)
                val contentColor = Color.Black

                Card(
                    onClick = {
                        viewModel.getConversionRate(item.first)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = backgroundColor,
                        contentColor = contentColor
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItemPlacement(tween(500))

                ) {
                    SingleItem(topCard, item, contentColor)
                }
            }
        }
        if (isLoading == true) {
            CircularProgressIndicator(modifier = modifier.align(alignment = Alignment.Center))
        }
    }
}

@Composable
private fun SingleItem(
    topCard: Pair<String, Double>,
    item: Pair<String, Double>,
    contentColor: Color
) {
    val topCardCurrency = topCard.first
    val topCardAmount = topCard.second
    val itemCurrency = item.first
    val itemAmount = item.second

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.first,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = item.second.times(topCardAmount).toCurrencyWithoutSymbol(itemCurrency),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                color = contentColor.copy(alpha = 0.6f),
                text = item.first.toCurrencyFullName(),
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                color = contentColor.copy(alpha = 0.6f),
                text = "${1.0.toCurrency(topCardCurrency)} = ${
                    itemAmount.toCurrencyWithoutSymbol(
                        itemCurrency
                    )
                }",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
private fun SingleTopItem(
    topCard: Pair<String, Double>,
    contentColor: Color,
    viewModel: ConverterViewModel
) {
    val topCardCurrency = topCard.first
    var topCardAmount by rememberSaveable { mutableStateOf(DEFAULT_STARTING_VALUE.toString()) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = topCardCurrency,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    color = contentColor.copy(alpha = 0.6f),
                    text = topCardCurrency.toCurrencyFullName(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            BasicTextField(
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                value = topCardAmount,
                onValueChange = { it->
                    topCardAmount = it
                    viewModel.convertAmount(it.toDoubleOrNull() ?: 0.0)
                },
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.End,
                    color = contentColor
                ),

                )
        }

    }
}
