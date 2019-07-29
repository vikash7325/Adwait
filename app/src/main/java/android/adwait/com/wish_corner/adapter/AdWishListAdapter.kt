package android.adwait.com.wish_corner.adapter

import android.adwait.com.R
import android.adwait.com.donation.view.ADDonationActivity
import android.adwait.com.wish_corner.model.ADWishModel
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView

class AdWishListAdapter(val context: Context, var mWishList: List<ADWishModel>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        var vi = inflater.inflate(R.layout.wish_item, parent, false)

        val item: ADWishModel = getItem(position) as ADWishModel

        val name = vi.findViewById<TextView>(R.id.name)
        val age = vi.findViewById<TextView>(R.id.age)
        val ngo_name = vi.findViewById<TextView>(R.id.ngo_name)
        val wish_item = vi.findViewById<TextView>(R.id.wish_item)
        val donate = vi.findViewById<TextView>(R.id.donate)
        val headerLayout = vi.findViewById<LinearLayout>(R.id.header_layout)
        val contentLayout = vi.findViewById<LinearLayout>(R.id.child_view)

        if (position == 0) {
            headerLayout.visibility = View.VISIBLE
        } else {
            headerLayout.visibility = View.GONE
        }

        if (position == (mWishList.size - 1)) {
            contentLayout.setBackgroundResource(R.drawable.bottom_rounded_bg)
        }

        name.setText(item.name)
        age.setText(item.age.toString() + "")
        ngo_name.setText(item.ngo)
        wish_item.setText(item.wishItem)

        donate.setOnClickListener(View.OnClickListener {
            var donation = Intent(context, ADDonationActivity::class.java)
            donation.putExtra("amount",item.price)
            context.startActivity(donation)
        })
        return vi
    }

    override fun getItem(position: Int): Any {

        return mWishList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        if (mWishList.size == 0) {
            return 0
        }
        return mWishList.size
    }
}