package com.coder_x.connect

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.coder_x.connect.databinding.FragmentEditProfileBinding


class EditProfileFragment : Fragment(), ImageHelper.OnImageSelectedListener {
    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var prefsHelper: SharedPrefsHelper
    private lateinit var fragmentManager: FragmentManager
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        prefsHelper = SharedPrefsHelper(requireContext())
        val imageHelper = ImageHelper(this)
        fragmentManager = requireActivity().supportFragmentManager

        // open Gallery
        binding.editImage.setOnClickListener {
            imageHelper.checkGalleryPermission()
        }

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

        val empName = prefsHelper.getEmployeeName()
        var empID = prefsHelper.getEmployeeId()
        val empDepartment = prefsHelper.getEmpDepartment()
        val mobileNumber = prefsHelper.getMobileNumber()
        val empImagePath = prefsHelper.getEmployeeImageUri()
        val workHours = prefsHelper.getEmpHours()

        binding.employeeNameEditText.setText(empName)
        binding.departmentEditText.setText(empDepartment)
        binding.mobileEditText.setText(mobileNumber)
        binding.workHoursEditText.setText(workHours.toString())
        binding.employeeImage.setImageURI(empImagePath)



        binding.saveButton.setOnClickListener {
            val employeeName = binding.employeeNameEditText.text.toString()
            val department = binding.departmentEditText.text.toString()
            val mobile_number = binding.mobileEditText.text.toString()
            val workHour = binding.workHoursEditText.text.toString().toInt()

            prefsHelper.putEmpName(employeeName)
            prefsHelper.putEmpDepartment(department)
            prefsHelper.putEmpMobile(mobile_number)
            prefsHelper.putEmpHours(workHour)
            // back To Profile Fragment
            requireActivity().supportFragmentManager.commit {
                replace(
                    R.id.fragment_container,
                    ProfileFragment()
                )
                addToBackStack(null)
            }

        }
        binding.backBtn.setOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0) {
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
            }

        }

        return binding.root

    }

    override fun onImageSelected(uri: Uri) {
        imageUri = uri
        binding.employeeImage.setImageURI(uri)

        // استخدام الدالة الجديدة لحفظ الصورة كـ Uri وBase64 معًا
        prefsHelper.putEmployeeImage(requireContext(), uri)
    }

}