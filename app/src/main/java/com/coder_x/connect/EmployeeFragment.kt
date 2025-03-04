package com.coder_x.connect

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.coder_x.connect.DataBaseHelper.insertEmployee
import com.coder_x.connect.databinding.FragmentEmployeeBinding
import com.yalantis.ucrop.UCrop
import java.io.File

class EmployeeFragment : Fragment() {
    private lateinit var binding: FragmentEmployeeBinding
    private lateinit var empName: String
    private lateinit var empDepartment: String
    private lateinit var empMobile: String
    private var workHours: Int = 0
    private lateinit var prefsHelper: SharedPrefsHelper
    private var imageUri: Uri? = null

    // إنشاء متغير لاستقبال نتيجة اختيار الصورة من المعرض
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                startCropActivity(uri)
            }
        }
    }

    // إنشاء متغير لاستقبال نتيجة طلب الإذن
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(requireContext(), "يرجى منح إذن الوصول للصور", Toast.LENGTH_SHORT).show()
        }
    }

    // إنشاء متغير لاستقبال نتيجة تحرير الصورة
    private val cropImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // الحصول على مسار الصورة المحررة
            UCrop.getOutput(result.data!!)?.let { uri ->
                imageUri = uri
                // عرض الصورة المحررة
                binding.empImage.setImageURI(uri)
                // تفعيل زر التسجيل بعد اختيار الصورة
                binding.RegisterBtn.visibility = View.VISIBLE
                // حفظ مسار الصورة في SharedPreferences
                prefsHelper.putEmpImagePath(uri.toString())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        val prefsHelper = SharedPrefsHelper(requireContext())
        this.prefsHelper = prefsHelper
        binding = FragmentEmployeeBinding.inflate(inflater, container, false)

        // 🔹 عند الضغط على صورة الموظف، يتم فتح معرض الصور
        binding.empImage.setOnClickListener {
            checkGalleryPermission()
        }

        binding.RegisterBtn.visibility = View.INVISIBLE

        binding.RegisterBtn.setOnClickListener {
            empName = binding.EmployeeName.text.toString()
            empDepartment = binding.EmployeeDepartment.text.toString()
            empMobile = binding.EmployeeMobileNumber.text.toString()
            workHours = binding.EmployeeWorkHours.text.toString().toInt()

            prefsHelper.putEmpName(empName)
            prefsHelper.putEmpDepart(empDepartment)
            prefsHelper.putEmpMobile(empMobile)
            prefsHelper.putEmpHours(workHours)
            prefsHelper.putEmpID(0)
            prefsHelper.setSetupCompleted(true)

            // إضافة التحقق من وجود صورة
            if (imageUri != null) {
                prefsHelper.putEmpImagePath(imageUri.toString())
            }

            insertEmployee(
                requireContext(), empName, empDepartment, empMobile, workHours
            )
        }

        binding.FinishBtn.setOnClickListener {
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        binding.PreviousBtn.setOnClickListener {
            (activity as? SetupActivity)?.prevPage()
        }

        return binding.root
    }

    // التحقق من صلاحية الوصول إلى معرض الصور
    private fun checkGalleryPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    // فتح معرض الصور
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    // بدء نشاط تحرير الصورة
    private fun startCropActivity(sourceUri: Uri) {
        val destinationUri = Uri.fromFile(
            File(
                requireContext().cacheDir,
                "cropped_image_${System.currentTimeMillis()}.jpg"
            )
        )

        // استخدام مكتبة UCrop لتحرير الصورة
        val uCropIntent = UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f) // نسبة 1:1 للصورة
            .withMaxResultSize(1000, 1000) // الحد الأقصى للحجم
            .getIntent(requireContext())

        cropImageLauncher.launch(uCropIntent)
    }
}