package app.cloudy.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import app.cloudy.Screens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.nio.FloatBuffer
import java.time.LocalDateTime


@Composable
fun LoadingScreen(
    navController: NavController,
    imageUri: MutableState<Uri>,
    result: MutableState<String>
) {
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        isLoading.value = true
        asyncPredict(context, navController, imageUri.value, result)
        isLoading.value = false
    }

    // UI layout
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
    }
}

suspend fun asyncPredict(
    context: Context,
    navController: NavController,
    imageUri: Uri,
    result: MutableState<String>
) {
    withContext(Dispatchers.IO) {

        val tensor = preprocessImageUri(context, imageUri)


        val model = loadModelFromAssets(context, "model_final.pt")

        val outputTensor = model.forward(IValue.from(tensor)).toTensor()
        val scores = outputTensor.dataAsFloatArray

        val prediction = postprocessOutput(scores)

        val weatherPrediction = getWeatherPrediction(prediction, LocalDateTime.now(), context)

        result.value = "Recognised cloud is:\n $prediction\n Weather forecast:\n $weatherPrediction"
    }
    navController.navigate(Screens.Result.name)
}

fun preprocessImageUri(context: Context, imageUri: Uri): Tensor {
    val inputStream = context.contentResolver.openInputStream(imageUri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream?.close()

    val (newWidth, newHeight) = calculateNewDimensions(
        originalBitmap.width,
        originalBitmap.height,
        400
    )

    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
    val bufferSize = 3 * newWidth * newHeight
    val floatBuffer = FloatBuffer.allocate(bufferSize)

    for (y in 0 until newHeight) {
        for (x in 0 until newWidth) {
            val pixel = resizedBitmap.getPixel(x, y)
            floatBuffer.put(Color.red(pixel) / 255.0f)
            floatBuffer.put(Color.green(pixel) / 255.0f)
            floatBuffer.put(Color.blue(pixel) / 255.0f)
        }
    }
    floatBuffer.rewind()

    val shape = longArrayOf(1, 3, newHeight.toLong(), newWidth.toLong())
    return Tensor.fromBlob(floatBuffer.array(), shape)
}

fun calculateNewDimensions(width: Int, height: Int, targetSize: Int): Pair<Int, Int> {
    return if (width < height) {
        val newHeight = (height * targetSize) / width
        targetSize to newHeight
    } else {
        val newWidth = (width * targetSize) / height
        newWidth to targetSize
    }
}


fun postprocessOutput(scores: FloatArray): String {
    val cloudTypes = arrayOf(
        "Cirrus",
        "Cirrostratus",
        "Cirrocumulus",
        "Altocumulus",
        "Altostratus",
        "Cumulus",
        "Cumulonimbus",
        "Nimbostratus",
        "Stratocumulus",
        "Stratus"
    )

    var maxValue = scores[0]
    var maxIndex = 0

    for (i in scores.indices) {
        if (scores[i] > maxValue) {
            maxValue = scores[i]
            maxIndex = i
        }
    }

    var cloudName = cloudTypes[maxIndex % 10]


    return cloudName
}

fun loadModelFromAssets(context: Context, modelFilePath: String): Module {
    return Module.load(assetFilePath(context, modelFilePath))
}

fun assetFilePath(context: Context, assetName: String): String {
    val file = File(context.filesDir, assetName)
    context.assets.open(assetName).use { inputStream ->
        FileOutputStream(file).use { outputStream ->
            val buffer = ByteArray(4 * 1024)
            var read: Int
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
        }
    }
    return file.absolutePath
}


fun getWeatherPrediction(cloudName: String, date: LocalDateTime, context: Context): String {
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
    date.monthValue
    val season: String = if (date.monthValue < 5) {
        "winter"
    } else if (date.monthValue < 11) {
        "summer"
    } else {
        "winter"
    }
    var weather_prediction = "none"
    var weather_description = "none"
    var image_path = "none"

    for (i in 0..<weather_prediction_map.length()) {
        val weather = (weather_prediction_map[i] as JSONObject)

        if (weather.get("cloud_id") == cloud_id && weather.get("season") == season) {
            weather_prediction = weather.get("weather_prediction").toString()
            weather_description = weather.get("weather_description").toString()
            image_path = weather.get("image_path").toString()
            break
        }
    }

//    return "$type;\n$description;\n$weather_prediction;\n$weather_description;\n$image_path"
    return weather_description
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
