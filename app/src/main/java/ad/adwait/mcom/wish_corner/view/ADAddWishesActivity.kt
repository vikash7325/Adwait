package ad.adwait.mcom.wish_corner.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import ad.adwait.mcom.R
import ad.adwait.mcom.wish_corner.model.ADWishModel
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import android.text.TextUtils
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_wishes.*
import java.util.*

class ADAddWishesActivity : ADBaseActivity() {
    private var mIsEdit = false
    private var mKey = ""
    private lateinit var mProgressDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_wishes)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener { onBackPressed() })

        dob.setOnClickListener(View.OnClickListener {
            dob.hideKeyboard()
            showCalendar()
        })

        if (intent != null && intent.hasExtra("wishData")) {
            var data = intent.getParcelableExtra<ADWishModel>("wishData")
            mIsEdit = true
            mKey = data.keyId
            child_name.setText(data.name)
            dob.setText(data.birthday)
            ngo_name.setText(data.ngo)
            wish_item.setText(data.wishItem)
            item_price.setText(data.price.toString())

        }else
        {
            mIsEdit = false
        }

        submit_btn.setOnClickListener(View.OnClickListener {

            val name = child_name.text.toString()
            val doBirth = dob.text.toString()
            val ngo = ngo_name.text.toString()
            val wish = wish_item.text.toString()
            val price = item_price.text.toString()

            item_price.hideKeyboard()


            if (TextUtils.isEmpty(name)) {
                child_name.error = getString(R.string.empty_username)
            } else if (TextUtils.isEmpty(doBirth)) {
                dob.error = getString(R.string.empty_dob)
            } else if (TextUtils.isEmpty(ngo)) {
                ngo_name.error = getString(R.string.empty_ngo)
            } else if (TextUtils.isEmpty(wish)) {
                wish_item.error = getString(R.string.empty_wish_item)
            } else if (TextUtils.isEmpty(price) || price.toInt() == 0) {
                item_price.error = getString(R.string.empty_price)
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), add_wish_parent, true)
                    return@OnClickListener
                }
                mProgressDialog = showProgressDialog("", false)
                mProgressDialog.show()

                val user = MySharedPreference(this).getValueString(getString(R.string.userName))
                val wishModel = ADWishModel(
                    name, doBirth, getAge(doBirth, "dd-MM-yyyy"), ngo, wish, price.toInt(),
                    "", "", user.toString()
                )
                val mWishesTable =
                    FirebaseDatabase.getInstance().reference.child("Wish_Corner")

                var key = mWishesTable.push().key.toString()
                if(!mIsEdit) {
                    wishModel.keyId = key
                }else{
                    key =mKey
                }
                mWishesTable.child(key).setValue(wishModel)
                    .addOnSuccessListener {
                        hideProgress(mProgressDialog)
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    .addOnFailureListener {
                        hideProgress(mProgressDialog)
                    }
            }
        })
    }

    private fun showCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        val dpd = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                dob.setText("" + dayOfMonth + "-" + monthOfYear + "-" + year)
            },
            year,
            month,
            day
        )
        dpd.datePicker.maxDate = System.currentTimeMillis() + 1000
        dpd.show()
    }

}