package android.adwait.com.admin.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.admin.adapter.ADTransferMessagesAdapter
import android.adwait.com.admin.model.ADAddChildModel
import android.adwait.com.admin.model.ADTransferData
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.appcompat.widget.Toolbar
import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_routing_layout.*
import java.util.*

class ADRoutingMessagesActivity : ADBaseActivity() {

    var mChildListData: HashMap<String, ADAddChildModel> = HashMap<String, ADAddChildModel>()
    var messageData: HashMap<String, ADTransferData> = HashMap<String, ADTransferData>()
    private lateinit var mProgressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routing_layout)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        if (intent != null && intent.hasExtra("messageData")) {

            messageData =
                intent.getSerializableExtra("messageData") as HashMap<String, ADTransferData>
            getData()
        }
    }


    fun getData() {

        mProgressDialog = showProgressDialog("", false)
        mProgressDialog.show()

        val mChildDataTable =
            FirebaseDatabase.getInstance()
                .reference.child((this).CHILD_TABLE_NAME)

        mChildDataTable.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot != null) {
                        mChildListData.clear()
                        for (data in dataSnapshot.children) {
                            var childData = data.getValue(ADAddChildModel::class.java)!!
                            val key = data.key.toString()
                            mChildListData.put(key, childData)
                        }

                        val messagesAdapter = ADTransferMessagesAdapter(mChildListData, messageData)

                        recycler_view.itemAnimator = (androidx.recyclerview.widget.DefaultItemAnimator())
                        recycler_view.addItemDecoration(
                            androidx.recyclerview.widget.DividerItemDecoration(
                                applicationContext,
                                androidx.recyclerview.widget.DividerItemDecoration.VERTICAL
                            )
                        )
                        recycler_view.adapter = messagesAdapter
                    }
                }
                hideProgress(mProgressDialog)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("Testing==>", error.message)
                hideProgress(mProgressDialog)
            }

        })
    }


}