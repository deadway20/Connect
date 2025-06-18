package com.coder_x.connect.profile

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.coder_x.connect.R
import com.coder_x.connect.databinding.ActivityEditProfileBinding
import com.coder_x.connect.helpers.ImageHelper
import com.coder_x.connect.helpers.LocaleHelper
import com.coder_x.connect.helpers.SharedPrefsHelper


class EditProfileActivity : AppCompatActivity() {

    override fun attachBaseContext(baseContext: Context) {
        super.attachBaseContext(LocaleHelper.getLocalizedContext(baseContext))
    }
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var prefsHelper: SharedPrefsHelper
    private lateinit var fragmentManager: FragmentManager
    private lateinit var imageHelper: ImageHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)

        prefsHelper = SharedPrefsHelper(this)
        fragmentManager = supportFragmentManager
        imageHelper = ImageHelper(Fragment())

        // استرجاع صورة الموظف
        val imagePath = prefsHelper.getEmployeeImageUri()
        if (imagePath != null) {
            try {
                // تحويل URI إلى Bitmap
                val imageUri = imagePath
                binding.employeeImage.setImageURI(imageUri)
            } catch (e: Exception) {
                // معالجة الأخطاء المحتملة
                Log.e("MainActivity", "Error loading image: ${e.message}")
                // يمكنك وضع صورة افتراضية في حالة حدوث خطأ
                binding.employeeImage.setImageResource(R.drawable.emp_img)
            }
        }

        var empName = prefsHelper.getEmployeeName()
        var empID = prefsHelper.getEmployeeId()
        var empDepartment = prefsHelper.getEmpDepartment()
        var mobileNumber = prefsHelper.getMobileNumber()
        var empImagePath = prefsHelper.getEmployeeImageUri()

        binding.employeeNameEditText.setText(empName)
        binding.departmentEditText.setText(empDepartment)
        binding.mobileEditText.setText(mobileNumber)
        if (empImagePath != null) {
            binding.employeeImage.setImageURI(empImagePath)
        }


        binding.saveButton.setOnClickListener {
            val employeeName = binding.employeeNameEditText.text.toString()
            val department = binding.departmentEditText.text.toString()
            val mobile_number = binding.mobileEditText.text.toString()

            prefsHelper.putEmpName(employeeName)
            prefsHelper.putEmpDepartment(department)
            prefsHelper.putEmpMobile(mobile_number)
            // back To Profile Fragment
            finish()
//            supportFragmentManager.commit {
//                replace(R.id.fragment_container, ProfileFragment())
//                addToBackStack(null)
//            }

        }

    }

}