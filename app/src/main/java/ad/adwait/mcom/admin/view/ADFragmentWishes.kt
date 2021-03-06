package com.example.myapplication.admin.view

import and.com.polam.utils.ADBaseActivity
import ad.adwait.mcom.R
import ad.adwait.mcom.admin.view.ADAdminActivity
import ad.adwait.mcom.wish_corner.model.ADWishModel
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_layout.*


class ADFragmentWishes : androidx.fragment.app.Fragment(), ad.adwait.mcom.utils.WishListItemTouchHelper.RecyclerItemTouchHelperListener {

    companion object {
        @JvmStatic
        fun newInstance(): ADFragmentWishes {
            return ADFragmentWishes()
        }
    }

    private lateinit var wishAdapter: ad.adwait.mcom.admin.adapter.ADWishListAdapter
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
        wishAdapter = ad.adwait.mcom.admin.adapter.ADWishListAdapter(activity, mWishListData)

        recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        recycler_view.itemAnimator = (androidx.recyclerview.widget.DefaultItemAnimator())
        recycler_view.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(
                activity,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            )
        )
        recycler_view.adapter = wishAdapter

        var callBack: ItemTouchHelper.SimpleCallback =
            ad.adwait.mcom.utils.WishListItemTouchHelper(0, ItemTouchHelper.LEFT, this)

        ItemTouchHelper(callBack).attachToRecyclerView(recycler_view)
        getData()
    }


    override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, direction: Int, position: Int) {

        if (viewHolder is ad.adwait.mcom.admin.adapter.ADWishListAdapter.MyViewHolder) {

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
                            var wishData = data.getValue(ADWishModel::class.java)!!
                            wishData.keyId = data.key.toString()
                            mWishListData.add(wishData)
                        }
                        wishAdapter.notifyDataSetChanged()
                    } else {
                        Log.i("Testing==>", "Data null")
                        error_msg.visibility = View.VISIBLE
                    }
                } else {
                    Log.i("Testing==>", "Data does not exists")
                    error_msg.visibility = View.VISIBLE
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
        (activity as ADAdminActivity).showHideProgress(true)

        wishesDataTable.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {
                    appleSnapshot.ref.removeValue()
                }
                (activity as ADAdminActivity).showHideProgress(false)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("Testing==>", error.message)
                (activity as ADAdminActivity).showHideProgress(false)
            }
        })
    }

}