package android.adwait.com.wish_corner.view

import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.adwait.com.R
import android.adwait.com.wish_corner.model.ADWishModel
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_wishes.*
import java.util.*

class ADAddWishesActivity : ADBaseActivity() {

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
                progress_layout.visibility = View.VISIBLE
                val user = MySharedPreference(this).getValueString(getString(R.string.userName))
                val wishModel = ADWishModel(
                    name, doBirth, getAge(doBirth, "dd-MM-yyyy"), ngo, wish, price.toInt(),
                    "", "", user.toString()
                )
                val mWishesTable =
                    FirebaseDatabase.getInstance().reference.child("Wish_Corner")

                val key = mWishesTable.push().key.toString()

                mWishesTable.child(key).setValue(wishModel)
                    .addOnSuccessListener {
                        progress_layout.visibility = View.GONE
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    .addOnFailureListener {
                        progress_layout.visibility = View.GONE
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

        dpd.show()
    }

}