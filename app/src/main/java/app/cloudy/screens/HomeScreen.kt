package app.cloudy.screens

import android.Manifest
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.ImageSearch
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import app.cloudy.Screens
import coil.compose.AsyncImage
import java.io.File
import java.util.Date
import java.util.Objects

@Composable
fun HomeScreen(navController: NavController, imageUri: MutableState<Uri>) {
    val context = LocalContext.current
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> imageUri.value = uri ?: Uri.EMPTY }
    )
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider",
        file
    )
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) imageUri.value = uri
        }
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
//                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        Proceed(navController, imageUri, context)

        ShowImage(imageUri = imageUri.value)

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            TakePhoto(permissionLauncher = permissionLauncher)
            ChoosePhoto(photoPicker = singlePhotoPickerLauncher)
            ClearImage(imageUri)
        }
    }
}

@Composable
fun Proceed(navController: NavController, imageUri: MutableState<Uri>, context: Context) {
    Button(onClick = {
        if (imageUri.value != Uri.EMPTY) {
            navController.navigate(Screens.Loading.name)
        } else {
            Toast.makeText(context, "sky photo required", Toast.LENGTH_SHORT).show()
        }
    }) {
        Icon(Icons.Outlined.Calculate, "proceed")
    }
}

@Composable
fun ShowImage(imageUri: Uri) {
    AsyncImage(
        model = imageUri,
        contentDescription = null,
        modifier = Modifier.fillMaxHeight(0.85F),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun TakePhoto(permissionLauncher: ManagedActivityResultLauncher<String, Boolean>) {
    Button(onClick = {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }) {
        Icon(Icons.Outlined.PhotoCamera, "take a photo")
    }
}

@Composable
fun ChoosePhoto(photoPicker: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) {
    Button(
        onClick = {
            photoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }
    ) {
        Icon(Icons.Outlined.ImageSearch, "choose a photo")
    }
}

@Composable
fun ClearImage(imageUri: MutableState<Uri>) {
    Button(onClick = {
        imageUri.value = Uri.EMPTY
    }) {
        Icon(Icons.Outlined.Clear, "clear the image")
    }
}

fun Context.createImageFile(): File {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val imageFileName = "JPEG_" + timestamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir /* directory */
    )
}
