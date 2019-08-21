package com.example.myapplication.admin.view

import android.adwait.com.R
import android.adwait.com.admin.adapter.ADChildListAdapter
import android.adwait.com.admin.model.ADAddChildModel
import android.adwait.com.utils.ChildItemTouchHelper
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_layout.*

class ADFragmentChild: Fragment(), ChildItemTouchHelper.RecyclerItemTouchHelperListener {


    companion object {
        @JvmStatic
        fun newInstance(): ADFragmentChild {
            return ADFragmentChild()
        }
    }

    private lateinit var childAdapter: ADChildListAdapter
    private var mChildListData = ArrayList<ADAddChildModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_layout, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childAdapter = ADChildListAdapter(activity, mChildListData)

        recycler_view.layoutManager = LinearLayoutManager(activity)
        recycler_view.itemAnimator = (DefaultItemAnimator())
        recycler_view.addItemDecoration(DividerItemDecoration(activity,DividerItemDecoration.VERTICAL))
        recycler_view.adapter = childAdapter

        var callBack: ItemTouchHelper.SimpleCallback = ChildItemTouchHelper(0, ItemTouchHelper.LEFT,this)

        ItemTouchHelper(callBack).attachToRecyclerView(recycler_view)

        getData()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {

        if (viewHolder is ADChildListAdapter.MyViewHolder){

            val item = mChildListData.get(viewHolder.adapterPosition)
            val pos = viewHolder.adapterPosition
            childAdapter.removeItem(position)

            var snackbar = Snackbar.make(constraintLayout,"Deleted!!", Snackbar.LENGTH_LONG).setAction("UNDO",View.OnClickListener {
                childAdapter.restoreItem(item,position)
            })

            snackbar.setActionTextColor(Color.YELLOW).show()

        }
    }

    private fun getData(){


        childAdapter.notifyDataSetChanged()
    }
}