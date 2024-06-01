package app.cloudy.screens

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.cloudy.Screens
import kotlinx.coroutines.delay
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.time.LocalDateTime


@Composable
fun LoadingScreen(
    navController: NavController,
    imageUri: MutableState<Uri>,
    result: MutableState<String>
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        asyncPredict(context, navController, imageUri.value, result)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        IndeterminateCircularIndicator()
    }
}

suspend fun asyncPredict(
    context: Context,
    navController: NavController,
    imageUri: Uri,
    result: MutableState<String>,
) {

    var date = LocalDateTime.now()
    print(imageUri)
    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
    print(bitmap)

    var cloudName = "Stratus"
    val weather_prediction = getWeatherPrediction(cloudName, context)


    delay(100)
    result.value = "result: " + date + "\n" + weather_prediction
    navController.navigate(Screens.Result.name)
}

fun getWeatherPrediction(cloudName: String, context: Context): String {
    val cloudMapping = loadJSONFromAsset(context, "cloud_mapping.json")
    var cloudMappingMap = mutableMapOf<String, Any>()
    if (cloudMapping != null) {
        cloudMappingMap = parseJSONToMap(cloudMapping)
    }


    val cloudTypes: JSONArray = cloudMappingMap["cloud_types"] as JSONArray
    var cloud_id = -1
    var type = "none"
    var description = "none"

    for (i in 0..<cloudTypes.length()) {
        val cloud = (cloudTypes[i] as JSONObject)

        if (cloud.get("name") == cloudName) {
            cloud_id = i
            type = cloud.get("type").toString()
            description = cloud.get("description").toString()
            break
        }
    }

    val weather_prediction_map: JSONArray = cloudMappingMap["weather_prediction_map"] as JSONArray
    var weather_prediction = "none"
    var weather_description = "none"
    var image_path = "none"

    for (i in 0..<weather_prediction_map.length()) {
        val weather = (weather_prediction_map[i] as JSONObject)

        if (weather.get("cloud_id") == cloud_id) {
            weather_prediction = weather.get("weather_prediction").toString()
            weather_description = weather.get("weather_description").toString()
            image_path = weather.get("image_path").toString()
            break
        }
    }

    return "$cloudName;\n$type;\n$description;\n$weather_prediction;\n$weather_description;\n$image_path"
}

fun loadJSONFromAsset(context: Context, fileName: String): String? {
    return try {
        val inputStream = context.assets.open(fileName)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))

        val stringBuilder = StringBuilder()
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }

        inputStream.close()
        stringBuilder.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun parseJSONToMap(jsonString: String): MutableMap<String, Any> {
    val jsonObject = JSONObject(jsonString)
    val map = mutableMapOf<String, Any>()
    val keys = jsonObject.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val value = jsonObject.get(key)
        map[key] = value
    }
    return map
}

@Composable
fun IndeterminateCircularIndicator() {
    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}
