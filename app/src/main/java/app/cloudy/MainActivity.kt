package app.cloudy

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.cloudy.screens.HomeScreen
import app.cloudy.screens.LoadingScreen
import app.cloudy.screens.ResultScreen
import app.cloudy.ui.theme.CloudyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CloudyTheme {
                val navController = rememberNavController()
                val imageUri = remember {
                    mutableStateOf<Uri>(Uri.EMPTY)
                }
                val result = remember {
                    mutableStateOf("")
                }

                Scaffold(
                    topBar = { TopBar() },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screens.Home.name,
                        Modifier.padding(innerPadding),
                    ) {
                        composable(route = Screens.Home.name) {
                            HomeScreen(
                                navController = navController,
                                imageUri =  imageUri,
                            )
                        }
                        composable(route = Screens.Loading.name) {
                            LoadingScreen(
                                navController = navController,
                                imageUri = imageUri,
                                result = result,
                            )
                        }
                        composable(route = Screens.Result.name) {
                            ResultScreen(
                                navController = navController,
                                result = result,
                            )
                        }
                    }
                }
            }
        }
    }
}
