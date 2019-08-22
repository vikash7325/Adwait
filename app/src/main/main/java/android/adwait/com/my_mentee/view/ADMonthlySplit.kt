package android.adwait.com.my_mentee.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_monthly_split.*
import org.json.JSONObject

class ADMonthlySplit : ADBaseActivity() {

    private var splitData: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_monthly_split)

        if (intent != null && intent.hasExtra("data")) {
            splitData = intent.getStringExtra("data");
            processData()
        }
    }

    private fun processData() {
        val jsonObject = JSONObject(splitData)

        val keys = jsonObject.keys()
        monthly_slit.removeAllViews()

        while (keys.hasNext()) {
            val key = keys.next()
            if (jsonObject.get(key) != null) {
                val view =
                    View.inflate(
                        this, R.layout.monthly_split_item, null
                    )
                val amount = view.findViewById<TextView>(R.id.amount)
                val uses = view.findViewById<TextView>(R.id.uses)

                val amt = jsonObject.get(key).toString() + getString(R.string.rupees)
                amount.setText(amt)
                uses.setText(key.substring(0,1).toUpperCase() + key.substring(1))
                monthly_slit.addView(view)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val window = this.getWindow()
        window.setLayout(
            (getScreenDetails(false) * 0.8).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT)
    }
}