package com.coder_x.connect

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.coder_x.connect.databinding.FragmentServerBinding
import kotlin.concurrent.thread


class ServerFragment : Fragment() {

    private lateinit var binding: FragmentServerBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val Server_Username = "sa"
    private val Server_Password = "12345678"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentServerBinding.inflate(inflater, container, false)
        binding.ServerUsername.setText(Server_Username)
        binding.ServerPassword.setText(Server_Password)
        sharedPreferences =
            requireActivity().getSharedPreferences("ServerPrefs", Context.MODE_PRIVATE)
        loadServerSettings()


        binding.saveCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) saveServerSettings()
        }


        binding.ConnectBtn.setOnClickListener {
            val ip = binding.ServerIP.text.toString().trim()
            val port = binding.ServerPort.text.toString().trim()

            sharedPreferences.edit()
                .putString("serverAddress", ip)
                .putString("serverPort", port)
                .apply()


            if (ip.isEmpty() || port.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى إدخال الـ IP والـ Port", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            thread {
                val connection = DataBaseHelper.connect(ip, port)
                activity?.runOnUiThread {
                    if (connection != null) {
                        Toast.makeText(
                            requireContext(), "✅ تم الاتصال بالسيرفر بنجاح!", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(), "❌ فشل الاتصال بقاعدة البيانات!", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }




        binding.SNextBtn.setOnClickListener {
            (activity as? SetupActivity)?.nextPage()
        }

        return binding.root
    }

    private fun saveServerSettings() {
        with(sharedPreferences.edit()) {
            putString("IP", binding.ServerIP.text.toString())
            putString("PORT", binding.ServerPort.text.toString())
            apply()
        }
    }

    private fun loadServerSettings() {
        binding.ServerIP.setText(sharedPreferences.getString("IP", ""))
        binding.ServerPort.setText(sharedPreferences.getString("PORT", ""))
        binding.ServerUsername.setText(sharedPreferences.getString("USERNAME", ""))
        binding.ServerPassword.setText(sharedPreferences.getString("PASSWORD", ""))
    }

}
