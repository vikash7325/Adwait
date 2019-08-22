package android.adwait.com.startup.adapter

import android.adwait.com.R
import android.adwait.com.startup.model.ADHelpPagerModel
import android.content.Context
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

public class ADHelpPagerAdapter(val context: Context, val data: MutableList<ADHelpPagerModel>) : PagerAdapter() {

    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }


    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.help_pager_item, view, false)!!

        val imageView = imageLayout
            .findViewById(R.id.img_pager_item) as ImageView


        imageView.setImageResource(data[position].getImage_drawables())

        val header = imageLayout.findViewById(R.id.header) as TextView
        header.setText(data[position].getHeader())

        val textData = imageLayout.findViewById(R.id.textdata) as TextView
        textData.setText(data[position].getTextData())

        view.addView(imageLayout, 0)

        return imageLayout
    }
}