package com.coder_x.connect.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.FragmentManager
import com.coder_x.connect.databinding.ActivityBottomBarBinding
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import androidx.core.graphics.scale
import com.coder_x.connect.profile.ProfileFragment
import com.coder_x.connect.R
import com.coder_x.connect.calendar.CalendarFragment
import com.coder_x.connect.helpers.LocaleHelper
import com.coder_x.connect.helpers.SharedPrefsHelper
import com.coder_x.connect.social.SocialFragment
import com.coder_x.connect.todo.ToDoFragment


class BottomBarActivity : AppCompatActivity() {

    override fun attachBaseContext(baseContext: Context) {
        super.attachBaseContext(LocaleHelper.getLocalizedContext(baseContext))
    }

    lateinit var binding: ActivityBottomBarBinding
    private lateinit var prefsHelper: SharedPrefsHelper

    // intialize fragment manager
    private lateinit var fragmentManager: FragmentManager
    private val calendarFragment = CalendarFragment()
    private val profileFragment = ProfileFragment()
    private val defaultFragment = MainFragment()
    private val socialFragment = SocialFragment()
    private val todoFragment = ToDoFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBottomBarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefsHelper = SharedPrefsHelper(this)
        // Initialize the fragment manager
        fragmentManager = supportFragmentManager

        setupBottomBarCorners()

        // set default fragment
        navigateToFragment(defaultFragment)

        // R.id.profile
        val userImg = prefsHelper.getEmployeeImageBitmap()
        if (userImg != null) {
            // Increase the size of the profile image
            val scaledUserImg = userImg.scale(120, 120, false) // Adjust 120, 120 to your desired size
            val roundedDrawable = RoundedBitmapDrawableFactory.create(resources, scaledUserImg).apply {
                isCircular = true
            }
            binding.bottomNavigation.menu.findItem(R.id.profile).icon = roundedDrawable
        }
        //set navigation bar item select
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> {
                    navigateToFragment(profileFragment)
                    true
                }

                R.id.home_btn -> {
                    navigateToFragment(defaultFragment)
                    true
                }

                R.id.calendar -> {
                    navigateToFragment(calendarFragment)
                    true
                }

                R.id.todoList -> {
                    navigateToFragment(todoFragment)
                    true
                }

                R.id.social -> {
                    navigateToFragment(socialFragment)
                    true
                }

                else -> false
            }
        }

        binding.homeBtn.setOnClickListener {
            navigateToFragment(defaultFragment)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            when (it.getStringExtra("open_fragment")) {
                "profile" -> binding.bottomNavigation.selectedItemId = R.id.profile
                "calendar" -> binding.bottomNavigation.selectedItemId = R.id.calendar
                "todoList" -> binding.bottomNavigation.selectedItemId = R.id.todoList
                "social" -> binding.bottomNavigation.selectedItemId = R.id.social
                else -> binding.bottomNavigation.selectedItemId = R.id.home_btn
            }


        }
    }

    private fun setupBottomBarCorners() {
        val bottomBarBackground = binding.bottomNavigationAppBar.background as MaterialShapeDrawable
        bottomBarBackground.shapeAppearanceModel =
            bottomBarBackground.shapeAppearanceModel.toBuilder().apply {
                setAllCorners(CornerFamily.ROUNDED, 50f)
            }.build()
    }


    private fun navigateToFragment(fragment: androidx.fragment.app.Fragment) {
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
    }


}