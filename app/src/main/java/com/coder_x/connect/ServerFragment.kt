package com.coder_x.connect

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val prefsHelper = SharedPrefsHelper(requireContext())
        binding = FragmentServerBinding.inflate(inflater, container, false)
        loadServerSettings()


        binding.connectButton.setOnClickListener {
            val ip = binding.serverIP.text.toString().trim()
            val port = binding.serverPort.text.toString().trim()

            prefsHelper.putServerAddress(ip)
            prefsHelper.putServerPort(port)


            if (ip.isEmpty() || port.isEmpty()) {
                Toast.makeText(requireContext(), "يرجى إدخال الـ IP والـ Port", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            thread {
                val connection = DataBaseHelper.connect(requireContext(), ip, port)
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

        binding.nextButton.setOnClickListener {
            (activity as? SetupActivity)?.nextPage()
        }
        binding.previousButton.setOnClickListener {
            (activity as? SetupActivity)?.prevPage()
        }

        if (binding.saveCredentialsCheckBox.isChecked) {
            saveServerSettings()
        } else {
            prefsHelper.clearPrefs()

        }

        return binding.root
    }

    private fun saveServerSettings() {
        val prefsHelper = SharedPrefsHelper(requireContext())
        val ip = binding.serverIP.text.toString().trim()
        val port = binding.serverPort.text.toString().trim()

        if (ip.isEmpty() || port.isEmpty()) {
            Toast.makeText(requireContext(), "يرجى إدخال الـ IP والـ Port", Toast.LENGTH_SHORT)
                .show()
            return
        }
        prefsHelper.putServerAddress(ip)
        prefsHelper.putServerPort(port)
        prefsHelper.setSetupCompleted(true)
        (activity as? SetupActivity)?.nextPage()

    }

    private fun loadServerSettings() {
        val prefsHelper = SharedPrefsHelper(requireContext())
        binding.serverIP.setText(prefsHelper.getServerAddress())
        binding.serverPort.setText(prefsHelper.getServerPort())
    }

}
