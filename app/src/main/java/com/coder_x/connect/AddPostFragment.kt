package com.coder_x.connect

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.coder_x.connect.database.PostEntity
import com.coder_x.connect.database.PostViewModel
import com.coder_x.connect.databinding.FragmentAddPostBinding

class AddPostFragment : Fragment() {

    private lateinit var binding: FragmentAddPostBinding
    private lateinit var prefsHelper: SharedPrefsHelper
    private val postViewModel: PostViewModel by activityViewModels()

    private var postImagePath: String? = null
    private var isTextAdded = false

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
        prefsHelper.getEmployeeImageUri()?.let { imagePath ->
            try {
                binding.employeeImage.setImageURI(imagePath.toUri())
            } catch (e: Exception) {
                binding.employeeImage.setImageResource(R.drawable.emp_img)
            }
        } ?: binding.employeeImage.setImageResource(R.drawable.emp_img)

        binding.employeeName.text = prefsHelper.getEmployeeName()
        binding.addPostText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.addPostText.text.isNotEmpty()) {
                isTextAdded = true
                checkAddPostButtonEnable()
            }
        }
    }

    private fun setupListeners() {
        binding.addPostBtn.setOnClickListener { addPost() }
        binding.btnAddPhoto.setOnClickListener { getImageFromGallery() }
        binding.btnTakePhoto.setOnClickListener { getImageFromCamera() }
    }

    private fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
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
                    data?.data?.let { selectedImageUri ->
                        binding.addPostImage.apply {
                            setImageURI(selectedImageUri)
                            visibility = View.VISIBLE
                        }
                        postImagePath = selectedImageUri.toString()
                        binding.cardPostImage.visibility = View.VISIBLE
                        checkAddPostButtonEnable()
                    }
                }

                REQUEST_CAMERA -> {
                    (data?.extras?.get("data") as? Bitmap)?.let { photo ->
                        binding.addPostImage.apply {
                            setImageBitmap(photo)
                            visibility = View.VISIBLE
                        }
                        postImagePath = saveImageToGallery(photo)?.toString()
                        binding.cardPostImage.visibility = View.VISIBLE
                        checkAddPostButtonEnable()
                    }
                }
            }
        }
    }

    private fun addPost() {
        val employeeName = prefsHelper.getEmployeeName()
        val employeeId = prefsHelper.getEmployeeId()
        val postText = binding.addPostText.text.toString()
        val empImageBase64 = prefsHelper.getEmployeeImageUri()

        // هنا نضيف كود التحقق
        Log.d("AddPostFragment", "Employee Image Base64: ${empImageBase64?.take(20)}...")

        val post = PostEntity(
            employeeName = employeeName,
            employeeId = employeeId,
            postTime = System.currentTimeMillis(),
            postText = postText,
            postImagePath = postImagePath,
            likesCount = 0,
            commentsCount = 0,
            isLiked = false,
            employeeImage = empImageBase64
        )

        postViewModel.insert(post)
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun saveImageToGallery(bitmap: Bitmap): Uri? {
        val savedImageURL = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            "Post_Image_${System.currentTimeMillis()}",
            "Image taken for post"
        )
        return savedImageURL?.toUri()
    }

    private fun checkAddPostButtonEnable() {
        if (isTextAdded || postImagePath != null) {
            binding.addPostBtn.isEnabled = true
        } else {
            binding.addPostBtn.isEnabled = false
        }
    }

}
