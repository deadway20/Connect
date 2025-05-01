package com.coder_x.connect

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.FragmentManager
import com.coder_x.connect.databinding.ActivityEditProfileBinding


class EditProfileActivity : AppCompatActivity() {

    override fun attachBaseContext(baseContext: Context) {
        super.attachBaseContext(LocaleHelper.getLocalizedContext(baseContext))
    }

    private lateinit var prefsHelper: SharedPrefsHelper
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var imageHelper: ImageHelper
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefsHelper = SharedPrefsHelper(this)
        fragmentManager = supportFragmentManager


        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        viewDirection(binding.view)

        // استرجاع صورة الموظف
        val imagePath = prefsHelper.getEmployeeImageUri()
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

        var empName = prefsHelper.getEmployeeName()
        var empID = prefsHelper.getEmployeeId()
        var empDepartment = prefsHelper.getEmpDepartment()
        var mobileNumber = prefsHelper.getMobileNumber()
        var empImagePath = prefsHelper.getEmployeeImageUri()

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
            // back To Profile Fragment
            finish()
//            supportFragmentManager.commit {
//                replace(R.id.fragment_container, ProfileFragment())
//                addToBackStack(null)
//            }

        }

    }


    private fun viewDirection(view: View) {
        prefsHelper = SharedPrefsHelper(this)
        if (prefsHelper.getLanguage() == "en") {
            view.rotation = -20f
            view.pivotX = -50f
            view.pivotY = 80f

        } else {
            view.rotation = 20f
            view.pivotX = 1500f
            view.pivotY = 80f
        }
    }
}