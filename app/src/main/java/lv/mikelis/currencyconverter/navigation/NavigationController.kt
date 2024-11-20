package lv.mikelis.currencyconverter.navigation

import CurrencyScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationController(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = Converter.route){
        composable(Converter.route) {
            CurrencyScreen(
                modifier = modifier
            )
        }
    }}