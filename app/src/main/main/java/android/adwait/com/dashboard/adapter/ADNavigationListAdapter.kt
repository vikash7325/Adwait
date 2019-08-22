package android.adwait.com.dashboard.adapter

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.CommonUtils
import android.adwait.com.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ADNavigationListAdapter(val mContext: Context) : BaseAdapter() {

    lateinit var mNavigationItems: Array<String>
    lateinit var mNavigationImages: Array<Int>

    init {
        val resources = mContext.resources
        mNavigationItems = resources.getStringArray(R.array.navigation_items)
        mNavigationImages = arrayOf(
            R.drawable.ic_menu_home, R.drawable.ic_my_mentee, R.drawable.ic_menu_be_change,
            R.drawable.ic_menu_lamp, R.drawable.ic_menu_hclub, R.drawable.ic_menu_happy
            , R.drawable.ic_menu_partners, R.drawable.ic_menu_refer, R.drawable.ic_menu_thought,
            R.drawable.ic_menu_contact)
    }

    override fun getCount(): Int {
        if (mNavigationItems == null) {
            return 0
        } else {
            if ((mContext as ADBaseActivity).isLoggedInUser()) {
                return mNavigationItems.size
            } else {
                return mNavigationItems.size - 1
            }
        }
    }

    override fun getItem(position: Int): Any {
        return mNavigationItems[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(mContext)
        var vi = inflater.inflate(R.layout.navigation_view_item, parent, false)

        val image = vi.findViewById<ImageView>(R.id.item_image)
        val text = vi.findViewById<TextView>(R.id.item_text)

        text.setText(CommonUtils.getHtmlText(mNavigationItems[position]))
        image.setImageResource(mNavigationImages[position])

        return vi
    }

}