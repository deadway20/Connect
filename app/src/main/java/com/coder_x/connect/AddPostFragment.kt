package com.coder_x.connect

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.coder_x.connect.database.PostData
import com.coder_x.connect.database.PostViewModel
import com.coder_x.connect.databinding.FragmentAddPostBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.net.toUri

class AddPostFragment : Fragment() {

    private lateinit var binding: FragmentAddPostBinding
    private lateinit var prefsHelper: SharedPrefsHelper
    private val postViewModel: PostViewModel by activityViewModels()

    private var postImagePath: String? = null

    private val REQUEST_GALLERY = 100
    private val REQUEST_CAMERA = 101
    private val PERMISSION_CODE = 102

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAddPostBinding.inflate(inflater, container, false)
        prefsHelper = SharedPrefsHelper(requireContext())

        setupViews()
        setupListeners()

        return binding.root
    }

    private fun setupViews() {
        val imagePath = prefsHelper.getEmpImagePath()
        if (imagePath != null) {
            try {
                binding.employeeImage.setImageURI(Uri.parse(imagePath))
            } catch (e: Exception) {
                binding.employeeImage.setImageResource(R.drawable.emp_img)
            }
        }

        binding.employeeName.text = prefsHelper.getEmployeeName()
    }

    private fun setupListeners() {
        binding.addPostBtn.setOnClickListener {
            addPost()
        }

        binding.btnAddPhoto.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        binding.takePhoto.setOnClickListener {
            checkPermissionAndOpenCamera()
        }
    }

    private fun checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
        } else {
            openGallery()
        }
    }

    private fun checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        binding.btnAddPhoto.setImageURI(selectedImageUri)
                        postImagePath = selectedImageUri.toString()
                        binding.btnAddPhoto.visibility = View.VISIBLE
                    }
                }

                REQUEST_CAMERA -> {
                    val photo = data?.extras?.get("data") as? Bitmap
                    if (photo != null) {
                        val uri = saveImageToGallery(photo)
                        binding.postImage.setImageURI(uri)
                        postImagePath = uri.toString()
                        binding.postImage.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            "CapturedImage_${System.currentTimeMillis()}",
            null
        )
        return path.toUri()
    }

    private fun addPost() {
        val employeeName = prefsHelper.getEmployeeName()
        val employeeId = prefsHelper.getEmployeeId()
        val postText = binding.etPostContent.text.toString().trim()
        val postTime = getCurrentFormattedTime().toLong()

        if (postText.isEmpty() && postImagePath == null) {
            Toast.makeText(requireContext(), "اكتب بوست أو حط صورة", Toast.LENGTH_SHORT).show()
            return
        }

        val post = PostData(
            employeeName = employeeName,
            employeeId = employeeId,
            postTime = postTime,
            postText = postText,
            postImagePath = postImagePath,
            likesCount = 0,
            commentsCount = 0
        )

        postViewModel.insert(post)

        Toast.makeText(requireContext(), "تم نشر البوست بنجاح ✅", Toast.LENGTH_SHORT).show()

        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun getCurrentFormattedTime(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    // التعامل مع نتيجة طلب التصاريح
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "تم منح الإذن ✅", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "تم رفض الإذن ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
