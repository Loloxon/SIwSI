package app.cloudy.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.cloudy.Screens
import kotlinx.coroutines.delay
import java.time.LocalDateTime

@Composable
fun LoadingScreen(
    navController: NavController,
    imageUri: MutableState<Uri>,
    result: MutableState<String>
) {
    LaunchedEffect(Unit) {
        asyncPredict(navController, imageUri.value, result)
    }
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        IndeterminateCircularIndicator()
    }
}

suspend fun asyncPredict(
    navController: NavController,
    imageUri: Uri,
    result: MutableState<String>,
) {
    delay(3000)
    result.value = "result " + LocalDateTime.now()
    navController.navigate(Screens.Result.name)
}

@Composable
fun IndeterminateCircularIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}
