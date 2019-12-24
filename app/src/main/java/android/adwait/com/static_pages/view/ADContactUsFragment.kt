package android.adwait.com.static_pages.view

import and.com.polam.utils.ADBaseActivity
import android.adwait.com.R
import android.adwait.com.dashboard.view.ADDashboardActivity
import android.adwait.com.static_pages.model.ADContact
import android.adwait.com.utils.ADBaseFragment
import android.adwait.com.utils.ADConstants
import android.adwait.com.utils.ADViewClickListener
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_contact_us.*

class ADContactUsFragment : ADBaseFragment() {

    private val CONTACT_TABLE: String = "Contact_details"
    private lateinit var mProgressDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_contact_us, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        form_submit.setOnClickListener(View.OnClickListener {
            var userName = name.text.toString().trim()
            var cSubject = subject.selectedItem.toString().trim()
            var cContact = email_or_num.text.toString().trim()
            var cMessage = message.text.toString().trim()


            if (TextUtils.isEmpty(userName)) {
                name.setError(getString(R.string.empty_username));
            } else if (subject.selectedItemPosition == 0) {
                (activity as ADBaseActivity).showMessage(getString(R.string.empty_subject), contact_parent, true);
            } else if (TextUtils.isEmpty(cContact)) {
                email_or_num.setError(getString(R.string.empty_contact));
            } else if (!(activity as ADBaseActivity).isValidContactDetails(cContact)) {
                email_or_num.setError(getString(R.string.invalid_contact))
            } else if (TextUtils.isEmpty(cMessage)) {
                message.setError(getString(R.string.empty_message));
            } else {
                if (!(activity as ADBaseActivity).isNetworkAvailable()) {
                    (activity as ADBaseActivity).showMessage(getString(R.string.no_internet), contact_parent, true)
                    return@OnClickListener
                }

                mProgressDialog = (activity as ADBaseActivity).showProgressDialog("", false)
                mProgressDialog.show()

                val contact = ADContact(userName, cSubject, cContact, cMessage)

                val fireBaseInstance = FirebaseDatabase.getInstance()
                val fireBaseDB = fireBaseInstance.getReference(CONTACT_TABLE)

                val key = fireBaseDB.push().key.toString()

                fireBaseDB.child(key).setValue(contact)
                        .addOnSuccessListener {
                            (activity as ADBaseActivity).hideProgress(mProgressDialog)
                            (activity as ADBaseActivity).showAlertDialog(getString(R.string.contact_success),
                                getString(R.string.close),
                                "",
                                ADViewClickListener {
                                    (activity as ADDashboardActivity).menuAction(ADConstants.MENU_HOME,"")
                                })
                            name.setText("")
                            subject.setSelection(0)
                            email_or_num.setText("")
                            message.setText("")
                        }
                        .addOnFailureListener {
                            (activity as ADBaseActivity).hideProgress(mProgressDialog)
                            (activity as ADBaseActivity).showMessage(getString(R.string.contact_failed), contact_parent, true)
                        }
            }
        })
    }
}