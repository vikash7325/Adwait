package android.adwait.com.my_contribution.adapter

import android.adwait.com.R
import android.adwait.com.donation.model.ADDonationModel
import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView

class ADContributionAdapter(
    val mContext: Context,
    val mContributionList: ArrayList<ADDonationModel>) : BaseExpandableListAdapter() {


    override fun getGroupCount(): Int {
        return mContributionList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getGroup(groupPosition: Int): Any {
        return mContributionList.get(groupPosition).userName
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return mContributionList.get(groupPosition).childId
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }


    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        if (convertView == null) {
            val layoutInflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.contribution_list_parent, null)
        }
        val amount = convertView!!.findViewById<TextView>(R.id.cost)
        val paymentOption = convertView!!.findViewById<TextView>(R.id.payment_option)
        val groupIndicator = convertView!!.findViewById<ImageView>(R.id.indicator)

        val data = mContributionList.get(groupPosition)
        amount.setText( data.amount.toString()+mContext.getString(R.string.rupees))
        paymentOption.setText(data.paymentMethod)


        if (isExpanded){
            groupIndicator.setImageResource(R.drawable.expanded)
        }else{
            groupIndicator.setImageResource(R.drawable.collapsed)
        }

        return convertView
    }


    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        if (convertView == null) {
            val layoutInflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.contribution_list_child, null)
        }
        val id = convertView!!.findViewById<TextView>(R.id.transaction_id)
        val paymentDate = convertView!!.findViewById<TextView>(R.id.date)
        val tryAgain = convertView!!.findViewById<TextView>(R.id.try_again)
        val paymentStatus = convertView!!.findViewById<TextView>(R.id.status)

        val data = mContributionList.get(groupPosition)
        id.setText("Transaction ID : " + data.transactionId)
        paymentDate.setText("Date : " + data.date)

        val pStatus = if (data.status) "Successful" else "Retry"

        paymentStatus.setText(pStatus)

        if (data.status){
            paymentStatus.setTextColor(ContextCompat.getColor(mContext,R.color.successful))
            tryAgain.visibility = View.GONE
        }else{
            paymentStatus.setTextColor(ContextCompat.getColor(mContext,R.color.failed))
            tryAgain.visibility = View.VISIBLE
        }

        return convertView
    }


}