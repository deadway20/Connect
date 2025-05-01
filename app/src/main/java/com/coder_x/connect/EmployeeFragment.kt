package com.coder_x.connect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.coder_x.connect.DataBaseHelper.insertEmployee
import com.coder_x.connect.databinding.FragmentEmployeeBinding

class EmployeeFragment : Fragment(), ImageHelper.OnImageSelectedListener {
    private lateinit var binding: FragmentEmployeeBinding
    private lateinit var empName: String
    private lateinit var empDepartment: String
    private lateinit var empMobile: String
    private var workHours: Int = 0
    private lateinit var prefsHelper: SharedPrefsHelper
    private lateinit var imageHelper: ImageHelper
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeBinding.inflate(inflater, container, false)

        // تأكد من تهيئة prefsHelper
        prefsHelper = SharedPrefsHelper(requireContext())

        imageHelper = ImageHelper(this)
        imageHelper.setOnImageSelectedListener(this)

        binding.empImage.setOnClickListener {
            imageHelper.checkGalleryPermission()
        }

        binding.RegisterBtn.setOnClickListener {
            // التحقق من إدخال البيانات
            if (validateInputs()) {
                saveEmployeeData()
            }
        }

        binding.FinishBtn.setOnClickListener {
            navigateToMainActivity()
        }

        binding.PreviousBtn.setOnClickListener {
            (activity as? SetupActivity)?.prevPage()
        }

        return binding.root
    }

    private fun validateInputs(): Boolean {
        empName = binding.EmployeeName.text.toString().trim()
        empDepartment = binding.EmployeeDepartment.text.toString().trim()
        empMobile = binding.EmployeeMobileNumber.text.toString().trim()

        // التحقق من أن الحقول غير فارغة
        return when {
            empName.isEmpty() -> {
                binding.EmployeeName.error = "الرجاء إدخال اسم الموظف"
                false
            }

            empDepartment.isEmpty() -> {
                binding.EmployeeDepartment.error = "الرجاء إدخال القسم"
                false
            }

            empMobile.isEmpty() -> {
                binding.EmployeeMobileNumber.error = "الرجاء إدخال رقم الهاتف"
                false
            }

            binding.EmployeeWorkHours.text.toString().isEmpty() -> {
                binding.EmployeeWorkHours.error = "الرجاء إدخال ساعات العمل"
                false
            }

            else -> {
                workHours = binding.EmployeeWorkHours.text.toString().toInt()
                true
            }
        }
    }

    private fun saveEmployeeData() {
        // حفظ البيانات في SharedPreferences
        prefsHelper.putEmpName(empName)
        prefsHelper.putEmpDepartment(empDepartment)
        prefsHelper.putEmpMobile(empMobile)
        prefsHelper.putEmpHours(workHours)
        prefsHelper.putEmpID(0)

        // قم بتعيين SetupCompleted دائمًا على true
        prefsHelper.setSetupCompleted(true)

        // إضافة التحقق من وجود صورة
        if (imageUri != null) {
            prefsHelper.putEmployeeImageFromUri(requireContext(), imageUri!!)
        }

        // إدراج بيانات الموظف في قاعدة البيانات
        insertEmployee(
            requireContext(), empName, empDepartment, empMobile, workHours
        )

        // إظهار زر الإنهاء
        binding.FinishBtn.visibility = View.VISIBLE
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), BottomBarActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish() // إضافة هذا السطر للتأكد من إغلاق النشاط الحالي
    }

    override fun onImageSelected(uri: Uri) {
        imageUri = uri
        binding.empImage.setImageURI(uri)
        binding.RegisterBtn.visibility = View.VISIBLE

        // استخدام الدالة الجديدة لحفظ الصورة كـ Uri وBase64 معًا
        prefsHelper.putEmployeeImageFromUri(requireContext(), uri)
    }
}