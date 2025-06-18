package com.coder_x.connect.todo
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.coder_x.connect.onboarding.EmployeeDataFragment
import com.coder_x.connect.onboarding.ServerFragment
import com.coder_x.connect.onboarding.WelcomeFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments = listOf(
        WelcomeFragment(),
        ServerFragment(),
        EmployeeDataFragment()
    )

    override fun getItemCount(): Int = fragments.size
    override fun createFragment(position: Int): Fragment = fragments[position]
}
