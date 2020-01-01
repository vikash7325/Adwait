package com.example.myapplication.admin.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.admin.adapter.ADChildListAdapter
import android.adwait.com.admin.model.ADAddChildModel
import android.adwait.com.admin.view.ADAdminActivity
import android.adwait.com.utils.ChildItemTouchHelper
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class ADFragmentChild : androidx.fragment.app.Fragment(), ChildItemTouchHelper.RecyclerItemTouchHelperListener {


    companion object {
        @JvmStatic
        fun newInstance(): ADFragmentChild {
            return ADFragmentChild()
        }
    }

    private lateinit var childAdapter: ADChildListAdapter
    private var mChildListData = ArrayList<ADAddChildModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_layout, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childAdapter = ADChildListAdapter(activity, mChildListData)

        recycler_view.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(activity)
        recycler_view.itemAnimator = (androidx.recyclerview.widget.DefaultItemAnimator())
        recycler_view.addItemDecoration(
            androidx.recyclerview.widget.DividerItemDecoration(
                activity,
                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
            )
        )
        recycler_view.adapter = childAdapter

        var callBack: ItemTouchHelper.SimpleCallback =
            ChildItemTouchHelper(0, ItemTouchHelper.LEFT, this)

        ItemTouchHelper(callBack).attachToRecyclerView(recycler_view)

        getData(true)
    }

    override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder?, direction: Int, position: Int) {

        if (viewHolder is ADChildListAdapter.MyViewHolder) {

            val item = mChildListData.get(viewHolder.adapterPosition)
            val pos = viewHolder.adapterPosition
            childAdapter.removeItem(position)

            var snackbar = Snackbar.make(constraintLayout, "Deleted!!", Snackbar.LENGTH_LONG)
                .setAction("UNDO", View.OnClickListener {
                    childAdapter.restoreItem(item, pos)
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

    fun getData(checkData: Boolean) {

        (activity as ADAdminActivity).showHideProgress(true)

        val mChildDataTable =
            FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).CHILD_TABLE_NAME)

        mChildDataTable.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot != null) {
                        mChildListData.clear()
                        for (data in dataSnapshot.children) {
                            var childData = data.getValue(ADAddChildModel::class.java)!!
                            mChildListData.add(childData)
                        }
                        childAdapter.notifyDataSetChanged()
                    }else{
                        error_msg.visibility = View.VISIBLE
                    }
                }else {
                    error_msg.visibility = View.VISIBLE
                }
                if (checkData) {
                    (activity as ADAdminActivity).getPreviousMonth()
                } else {
                    (activity as ADAdminActivity).showHideProgress(false)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("Testing==>", error.message)
                if (checkData) {
                    (activity as ADAdminActivity).getPreviousMonth()
                } else {
                    (activity as ADAdminActivity).showHideProgress(false)
                }
            }

        })
        childAdapter.notifyDataSetChanged()
    }

    fun deleteFromDB(key: String) {
        val mChildDataTable =
            FirebaseDatabase.getInstance()
                .reference.child((activity as ADBaseActivity).CHILD_TABLE_NAME).child(key)
        (activity as ADAdminActivity).showHideProgress(true)
        mChildDataTable.addListenerForSingleValueEvent(object : ValueEventListener {

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