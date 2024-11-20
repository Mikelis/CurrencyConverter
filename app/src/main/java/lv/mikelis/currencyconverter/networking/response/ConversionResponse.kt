package lv.mikelis.currencyconverter.networking.response

data class ConversionResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: HashMap<String, Double>
)
