package com.coder_x.connect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.coder_x.connect.databinding.ActivityEditProfileBinding


class EditProfileActivity : AppCompatActivity() {
    private lateinit var prefsHelper: SharedPrefsHelper
    private lateinit var binding: ActivityEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefsHelper = SharedPrefsHelper(this)


        // استرجاع صورة الموظف
        val imagePath = prefsHelper.getEmpImagePath()
        if (imagePath != null) {
            try {
                // تحويل URI إلى Bitmap
                val imageUri = imagePath.toUri()
                binding.employeeImage.setImageURI(imageUri)


            } catch (e: Exception) {
                // معالجة الأخطاء المحتملة
                Log.e("MainActivity", "Error loading image: ${e.message}")
                // يمكنك وضع صورة افتراضية في حالة حدوث خطأ
                binding.employeeImage.setImageResource(R.drawable.emp_img)
            }
        }

        var empName = prefsHelper.getEmpName()
        var empID = prefsHelper.getEmpID()
        var empDepartment = prefsHelper.getEmpDepartment()
        var mobileNumber = prefsHelper.getMobileNumber()
        var empImagePath = prefsHelper.getEmpImagePath()

        binding.employeeNameEditText.setText(empName)
        binding.departmentEditText.setText(empDepartment)
        binding.mobileEditText.setText(mobileNumber)
        if (empImagePath != null) {
            binding.employeeImage.setImageURI(empImagePath.toUri())
        }


        binding.saveButton.setOnClickListener {
            val employeeName = binding.employeeNameEditText.text.toString()
            val department = binding.departmentEditText.text.toString()
            val mobile_number = binding.mobileEditText.text.toString()

            prefsHelper.putEmpName(employeeName)
            prefsHelper.putEmpDepartment(department)
            prefsHelper.putEmpMobile(mobile_number)

            finish()
            startActivity(Intent(this, ProfileActivity::class.java))


        }

    }
}