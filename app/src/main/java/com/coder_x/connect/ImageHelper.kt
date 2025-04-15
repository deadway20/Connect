package com.coder_x.connect

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yalantis.ucrop.UCrop
import java.io.File

class ImageHelper(private val fragment: Fragment) {

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cropImageLauncher: ActivityResultLauncher<Intent>
    private var imageUri: Uri? = null
    private var onImageSelectedListener: OnImageSelectedListener? = null

    init {
        initializeLaunchers()
    }

    private fun initializeLaunchers() {
        pickImageLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    startCropActivity(uri)
                }
            }
        }

        requestPermissionLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(
                    fragment.requireContext(),
                    "يرجى منح إذن الوصول للصور",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        cropImageLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                UCrop.getOutput(result.data!!)?.let { uri ->
                    imageUri = uri
                    onImageSelectedListener?.onImageSelected(uri)
                }
            }
        }
    }

    fun checkGalleryPermission() {
        when {
            ContextCompat.checkSelfPermission(
                fragment.requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    private fun startCropActivity(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(
            File(
                fragment.requireContext().cacheDir,
                "cropped_image_${System.currentTimeMillis()}.jpg"
            )
        )

        val uCropIntent = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .getIntent(fragment.requireContext())

        cropImageLauncher.launch(uCropIntent)
    }

    fun setOnImageSelectedListener(listener: OnImageSelectedListener) {
        onImageSelectedListener = listener
    }

    interface OnImageSelectedListener {
        fun onImageSelected(uri: Uri)
    }
}