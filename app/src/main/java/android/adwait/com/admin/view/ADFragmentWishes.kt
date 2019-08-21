package com.example.myapplication.admin.view

import android.adwait.com.R
import android.adwait.com.admin.adapter.ADWishListAdapter
import android.adwait.com.admin.model.ADAddChildModel
import android.adwait.com.utils.WishListItemTouchHelper
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

class ADFragmentWishes : Fragment(), WishListItemTouchHelper.RecyclerItemTouchHelperListener {

    companion object {
        @JvmStatic
        fun newInstance(): ADFragmentWishes {
            return ADFragmentWishes()
        }
    }

    private lateinit var wishAdapter: ADWishListAdapter
    private var mWishListData = ArrayList<ADAddChildModel>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_layout, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wishAdapter = ADWishListAdapter(activity, mWishListData)

        recycler_view.layoutManager = LinearLayoutManager(activity)
        recycler_view.itemAnimator = (DefaultItemAnimator())
        recycler_view.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        recycler_view.adapter = wishAdapter

        var callBack: ItemTouchHelper.SimpleCallback = WishListItemTouchHelper(0,ItemTouchHelper.LEFT,this)

        ItemTouchHelper(callBack).attachToRecyclerView(recycler_view)
        getData()
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {

        if (viewHolder is ADWishListAdapter.MyViewHolder){

            val item = mWishListData.get(viewHolder.adapterPosition)
            val pos = viewHolder.adapterPosition
            wishAdapter.removeItem(position)

            var snackbar = Snackbar.make(constraintLayout,"Deleted!!",Snackbar.LENGTH_LONG).setAction("UNDO",View.OnClickListener {
                wishAdapter.restoreItem(item,position)
            })

            snackbar.setActionTextColor(Color.YELLOW).show()

        }
    }

    private fun getData(){

        wishAdapter.notifyDataSetChanged()
    }

}