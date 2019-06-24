package android.adwait.com.be_the_change.adapter

import android.adwait.com.R
import android.adwait.com.be_the_change.model.ADOurCauseModel
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ADCausesListAdapter(val mContext: Context, val mListData: List<ADOurCauseModel>) :
    BaseAdapter() {


    override fun getCount(): Int {
        return mListData.size
    }

    override fun getItem(position: Int): Any {
        return mListData.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view = convertView
        val data: ADOurCauseModel = getItem(position) as ADOurCauseModel
        view = LayoutInflater.from(mContext).inflate(R.layout.our_causes_list_item, parent, false)
        var causeImage: ImageView = view.findViewById(R.id.cause_image)
        var heading: TextView = view.findViewById(R.id.heading)
        var content: TextView = view.findViewById(R.id.content)
        var clickHere: TextView = view.findViewById(R.id.click_for_more)
        heading.setText(data.heading)
        content.setText(data.content)
        causeImage.setImageResource(data.imageUrl)
        return view!!
    }


    class ViewHolder(val view: View) {
        var causeImage: ImageView = view.findViewById(R.id.cause_image)
        var heading: TextView = view.findViewById(R.id.heading)
        var content: TextView = view.findViewById(R.id.content)
        var clickHere: TextView = view.findViewById(R.id.click_for_more)
    }


}