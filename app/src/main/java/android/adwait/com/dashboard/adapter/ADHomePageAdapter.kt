package android.adwait.com.dashboard.adapter

import android.adwait.com.R
import android.content.Context
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

public class ADHomePageAdapter(val context: Context, val data: ArrayList<String>) : PagerAdapter() {

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
        val imageLayout = inflater.inflate(R.layout.home_pager_item, view, false)!!

        val imageView = imageLayout
            .findViewById(R.id.img_pager_item) as ImageView

        Glide.with(context).load(data.get(position)).diskCacheStrategy(
            DiskCacheStrategy.ALL).error(R.drawable.image_not_found).into(imageView)

        view.addView(imageLayout, 0)

        return imageLayout
    }
}