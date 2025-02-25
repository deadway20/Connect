package com.coder_x.connect

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.coder_x.connect.DataBaseHelper.insertEmployeeSimple
import com.coder_x.connect.databinding.FragmentEmployeeBinding

class EmployeeFragment : Fragment() {
    private lateinit var binding: FragmentEmployeeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var Emp_img: Drawable
    private lateinit var Emp_name: String
    private lateinit var Emp_depart: String
    private lateinit var Emp_mobile: String
    private var Emp_work_hours: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmployeeBinding.inflate(inflater, container, false)

        binding.RegisterBtn.setOnClickListener {
            Emp_img = binding.empImage.drawable
            Emp_name = binding.EmployeeName.text.toString()
            Emp_depart = binding.EmployeeDepartment.text.toString()
            Emp_mobile = binding.EmployeeMobileNumber.text.toString()
            Emp_work_hours = binding.EmployeeWorkHours.text.toString().toInt()

            sharedPreferences =
                requireActivity().getSharedPreferences("EmpPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit()
                .putBoolean("isSetupCompleted", true)
                .putString("Emp_img", Emp_img.toString())
                .putString("Emp_name", Emp_name)
                .putString("Emp_depart", Emp_depart)
                .putString("Emp_mobile", Emp_mobile)
                .putInt("Emp_work_hours", Emp_work_hours)
                .apply()

            // طباعة تفاصيل الاتصال بالسيرفر
            insertEmployeeSimple(
                requireContext(),
                Emp_name,
                Emp_depart,
                Emp_mobile,
                Emp_work_hours
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
}
