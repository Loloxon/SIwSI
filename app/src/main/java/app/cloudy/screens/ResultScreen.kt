package app.cloudy.screens

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import app.cloudy.Screens

@Composable
fun ResultScreen(navController: NavController, result: MutableState<String>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        ReturnHomeButton(navController = navController)
        ShowResult(result = result.value)
    }

}

@Composable
fun ShowResult(result: String) {
    Text(
        text = result,
        Modifier.verticalScroll(ScrollState(0)),
    )
}

@Composable
fun ReturnHomeButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate(Screens.Home.name)
        }
    ) {
        Icon(Icons.Outlined.Home, contentDescription = "return home")
    }
}
