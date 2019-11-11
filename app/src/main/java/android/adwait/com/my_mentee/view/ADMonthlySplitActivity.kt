package android.adwait.com.my_mentee.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.admin.model.ADMoneySplitUp
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.dialog_monthly_split.*
import org.json.JSONObject

class ADMonthlySplitActivity : ADBaseActivity() {

    private var splitData: ADMoneySplitUp = ADMoneySplitUp()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_monthly_split)

        if (intent != null && intent.hasExtra("data")) {
            val bundle = intent.getBundleExtra("data");
            splitData = bundle.getParcelable("splitData")
            processData()
        }
    }

    private fun processData() {
        val data = Gson().toJson(splitData)
        val jsonObject = JSONObject(data)

        val keys = jsonObject.keys()
        monthly_slit.removeAllViews()

        var count = 1
        while (keys.hasNext()) {
            val key = keys.next()
            if (jsonObject.get(key) != null) {
                val view =
                    View.inflate(
                        this, R.layout.monthly_split_item, null
                    )
                val sno = view.findViewById<TextView>(R.id.sno)
                val amount = view.findViewById<TextView>(R.id.amount)
                val uses = view.findViewById<TextView>(R.id.uses)

                val no = count.toString() + "."

                sno.setText(no)
                count++

                val amt =  getString(R.string.rupees) + jsonObject.get(key).toString()
                amount.setText(amt)

                var temp = key.substring(0, 1).toUpperCase() + key.substring(1)
                temp = temp.replace("Amount", " Amount")

                uses.setText(temp)
                monthly_slit.addView(view)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val window = this.getWindow()
        window.setLayout(
            (getScreenDetails(false) * 0.8).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }
}