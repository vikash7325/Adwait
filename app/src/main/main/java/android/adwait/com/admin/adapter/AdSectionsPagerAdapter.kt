package com.example.myapplication.admin.adapter

import android.adwait.com.R
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.myapplication.admin.view.ADFragmentChild
import com.example.myapplication.admin.view.ADFragmentWishes
import android.util.SparseArray
import android.view.ViewGroup

private val TAB_TITLES = arrayOf(
    R.string.admin_tab1,
    R.string.admin_tab2
)

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class AdSectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    var registeredFragments = SparseArray<Fragment>()

    override fun getItem(position: Int): Fragment {

        if (position == 0) {
            return ADFragmentChild.newInstance()
        } else {
            return ADFragmentWishes.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 2
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    public fun getRegisteredFragment(position: Int): Fragment {
        return registeredFragments.get(position)
    }
}