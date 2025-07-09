package com.coder_x.connect.profile

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.coder_x.connect.R
import com.coder_x.connect.databinding.FragmentProfileBinding
import com.coder_x.connect.helpers.ImageHelper
import com.coder_x.connect.helpers.LocaleHelper
import com.coder_x.connect.helpers.SharedPrefsHelper
import com.coder_x.connect.main.BottomBarActivity
import com.coder_x.connect.social.SocialFragment.Companion.fontCustomize

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var prefsHelper: SharedPrefsHelper
    private lateinit var imageHelper: ImageHelper
    private lateinit var fragmentManager: FragmentManager

    private var languageChanged = false
    private var initialSetup = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        prefsHelper = SharedPrefsHelper(requireContext())
        imageHelper = ImageHelper(this)
        fragmentManager = requireActivity().supportFragmentManager


        setupViews()
        setupLanguageSpinner()
        applyClickListeners()
        darkModeSwitch()
        return binding.root
    }

    private fun setupViews() {
        // Top bar binding and font customization
        fontCustomize(requireContext(), binding.topBarTitle)
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val imagePath = prefsHelper.getEmployeeImageBitmap()
        val empID = prefsHelper.getEmployeeId()
        if (imagePath != null) {
            binding.employeeImage.setImageBitmap(imagePath)
        } else {
            binding.employeeImage.setImageResource(R.drawable.emp_img)
        }
        binding.employeeName.text = prefsHelper.getEmployeeName()
        binding.employeeId.text = getString(R.string.id, empID.toString())
        binding.DarkModeSwitch.isChecked = prefsHelper.getTheme()

    }


    private fun setupLanguageSpinner() {
        val languages = LocaleHelper.getSupportedLanguages()
        val languageNames = languages.map { LocaleHelper.getDisplayLanguageName(it) }

        val adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, languageNames
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.languageSpinner.adapter = adapter

        // تحديد العنصر الحالي
        val currentLang = LocaleHelper.getPersistedLanguage(requireContext())
        binding.languageSpinner.setSelection(languages.indexOf(currentLang))

        // معالجة تغيير اللغة
        binding.languageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long,
                ) {
                    if (!initialSetup && languageChanged) {
                        val selectedLanguage = languages[position]
                        if (selectedLanguage != currentLang) {
                            changeAppLanguage(selectedLanguage)
                        }
                    }
                    if (initialSetup) initialSetup = false
                    languageChanged = true
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun changeAppLanguage(languageCode: String) {
        // تطبيق اللغة الجديدة
        LocaleHelper.setAppLocale(requireContext(), languageCode)

        // إعادة تشغيل التطبيق
        restartApp()
    }

    private fun restartApp() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(requireContext(), BottomBarActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                putExtra("SELECTED_TAB", R.id.profile) // للحفاظ على التبويب المفتوح
            }
            requireActivity().finish()
            startActivity(intent)
            requireActivity().overridePendingTransition(0, 0)
        }, 300)
    }

    private fun applyClickListeners() {
        binding.apply {
            editProfileText.setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, EditProfileFragment()).commit()
            }
            serverSettingText.setOnClickListener {
                fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, EditServerFragment()).commit()
            }
        }
    }

    private fun darkModeSwitch() {
        binding.DarkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                prefsHelper.setTheme(true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                prefsHelper.setTheme(false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

}