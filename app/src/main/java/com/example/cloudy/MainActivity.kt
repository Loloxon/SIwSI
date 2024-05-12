package com.example.cloudy

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import java.io.File
import java.io.FilePermission

class MainActivity : AppCompatActivity() {

    val REQUEST_CODE = 2000

    private lateinit var imageView : ImageView
    private lateinit var btnImageCapture : Button
    private lateinit var btnImageClear : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        btnImageCapture = findViewById(R.id.btnImageCapture)
        btnImageClear = findViewById(R.id.btnImageClear)

        btnImageCapture.setOnClickListener{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CODE)
        }

        btnImageClear.setOnClickListener{
            imageView.setImageDrawable(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){
            imageView.setImageBitmap(data.extras!!.get("data") as Bitmap)
        }
    }
}