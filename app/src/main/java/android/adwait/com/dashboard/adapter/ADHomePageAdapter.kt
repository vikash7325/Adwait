package android.adwait.com.dashboard.adapter

import android.adwait.com.R
import android.adwait.com.my_mentee.view.ADVideoPlayerActivity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
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

        val playBtn = imageLayout
            .findViewById(R.id.play_btn) as ImageView
        val imageUrl = data.get(position)
        if (imageUrl.contains("mp4") || imageUrl.contains("mkv")
            || imageUrl.contains("flv")|| imageUrl.contains("avi")
            || imageUrl.contains("wmv")) {
            imageView.setImageBitmap(retriveVideoFrameFromVideo(imageUrl));
            playBtn.visibility = View.VISIBLE

            playBtn.setOnClickListener{
                val intent = Intent(context, ADVideoPlayerActivity::class.java)
                intent.putExtra("videoUrl",imageUrl)
                context.startActivity(intent)
            }
        } else {
            Glide.with(context).load(imageUrl).diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).error(R.drawable.image_not_found).into(imageView)
        }

        view.addView(imageLayout, 0)

        return imageLayout
    }

    @Throws(Throwable::class)
    fun retriveVideoFrameFromVideo(videoPath: String): Bitmap? {
        var bitmap: Bitmap? = null
        var mediaMetadataRetriever: MediaMetadataRetriever? = null
        try {
            mediaMetadataRetriever = MediaMetadataRetriever()
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, HashMap())
            else
                mediaMetadataRetriever.setDataSource(videoPath)
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.message)
        } finally {
            mediaMetadataRetriever?.release()
        }
        return bitmap
    }
}