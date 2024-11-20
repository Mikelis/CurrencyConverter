package lv.mikelis.currencyconverter.navigation


interface Destination {
    val route: String
}

object Converter: Destination {
    override val route = "Converter"
}

