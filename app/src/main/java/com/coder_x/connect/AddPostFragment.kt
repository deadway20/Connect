package com.coder_x.connect

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.coder_x.connect.database.PostEntity
import com.coder_x.connect.database.PostViewModel
import com.coder_x.connect.databinding.FragmentAddPostBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddPostFragment : Fragment() {

    private lateinit var binding: FragmentAddPostBinding
    private lateinit var prefsHelper: SharedPrefsHelper
    private val postViewModel: PostViewModel by activityViewModels()

    private var postImagePath: String? = null

    private val REQUEST_GALLERY = 100
    private val REQUEST_CAMERA = 101

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
                binding.employeeImage.setImageURI(imagePath.toUri())
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
            getImageFromGallery()
        }

        binding.takePhoto.setOnClickListener {
            getImageFromCamera()
        }
    }

    private fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun getImageFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    val selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        binding.addPostImage.setImageURI(selectedImageUri)
                        postImagePath = selectedImageUri.toString()
                    }
                }
                REQUEST_CAMERA -> {
                    val photo = data?.extras?.get("data") as? Bitmap
                    if (photo != null) {
                        binding.addPostImage.setImageBitmap(photo)
                        // عشان تحفظ الصورة في ملف وتاخد مسارها
                        val imageUri = saveImageToGallery(photo)
                        postImagePath = imageUri.toString()
                    }
                }
            }
        }
    }

    private fun addPost() {
        val employeeName = prefsHelper.getEmployeeName()
        val employeeId = prefsHelper.getEmployeeId()
        val postText = binding.addPostText.text.toString().trim()
        val postTime = getCurrentTime()

        if (postText.isEmpty()) {
            binding.addPostText.error = "اكتب حاجة الأول"
            return
        }

        val post = PostEntity(
            employeeName = employeeName,
            employeeId = employeeId,
            postTime = postTime,
            postText = postText,
            postImagePath = postImagePath,
            likesCount = 0,
            commentsCount = 0,
            isLiked = false


        )

        postViewModel.insert(post)

        // بعد إضافة البوست نرجع على SocialFragment
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun saveImageToGallery(bitmap: Bitmap): Uri? {
        val savedImageURL = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            "Post_Image_" + System.currentTimeMillis(),
            "Image taken for post"
        )
        return savedImageURL?.toUri()
    }

    private fun getCurrentTime(): Long {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val currentDate = Date() // التاريخ الحالي
        return currentDate.time
    }

}
