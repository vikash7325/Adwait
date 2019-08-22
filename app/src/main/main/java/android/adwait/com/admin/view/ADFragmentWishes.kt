package com.example.myapplication.admin.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.admin.adapter.ADWishListAdapter
import android.adwait.com.admin.model.ADAddChildModel
import android.adwait.com.utils.WishListItemTouchHelper
import android.adwait.com.wish_corner.model.ADWishModel
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_layout.*


class ADFragmentWishes : Fragment(), WishListItemTouchHelper.RecyclerItemTouchHelperListener {

    companion object {
        @JvmStatic
        fun newInstance(): ADFragmentWishes {
            return ADFragmentWishes()
        }
    }

    private lateinit var wishAdapter: ADWishListAdapter
    private var mWishListData = ArrayList<ADWishModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_layout, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wishAdapter = ADWishListAdapter(activity, mWishListData)

        recycler_view.layoutManager = LinearLayoutManager(activity)
        recycler_view.itemAnimator = (DefaultItemAnimator())
        recycler_view.addItemDecoration(
            DividerItemDecoration(
                activity,
                DividerItemDecoration.VERTICAL
            )
        )
        recycler_view.adapter = wishAdapter

        var callBack: ItemTouchHelper.SimpleCallback =
            WishListItemTouchHelper(0, ItemTouchHelper.LEFT, this)

        ItemTouchHelper(callBack).attachToRecyclerView(recycler_view)
        getData()
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int, position: Int) {

        if (viewHolder is ADWishListAdapter.MyViewHolder) {

            val item = mWishListData.get(viewHolder.adapterPosition)
            val pos = viewHolder.adapterPosition
            wishAdapter.removeItem(position)

            var snackbar = Snackbar.make(constraintLayout, "Deleted!!", Snackbar.LENGTH_LONG)
                .setAction("UNDO", View.OnClickListener {
                    wishAdapter.restoreItem(item, pos)
                })

            snackbar.addCallback(object : Snackbar.Callback() {

                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (event != DISMISS_EVENT_ACTION) {
                        deleteFromDB(item.keyId)
                    }
                }

                override fun onShown(sb: Snackbar?) {
                    super.onShown(sb)
                }
            })

            snackbar.setActionTextColor(Color.YELLOW).show()

        }
    }

    fun getData() {

        val wishesDataTable =
            FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).WISH_CORNER_TABLE_NAME)

        wishesDataTable.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot != null) {
                        mWishListData.clear()
                        for (data in dataSnapshot.children) {
                            var childData = data.getValue(ADWishModel::class.java)!!
                            childData.keyId = data.key.toString()
                            mWishListData.add(childData)
                        }
                        wishAdapter.notifyDataSetChanged()
                    } else {
                        Log.i("Testing==>", "Data null")
                    }
                } else {
                    Log.i("Testing==>", "Data does not exists")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("Testing==>", error.message)
            }
        })
    }

    fun deleteFromDB(key: String) {
        val wishesDataTable =
            FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).WISH_CORNER_TABLE_NAME).child(key)

        wishesDataTable.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("Testing==>", error.message)
            }
        })
    }

}