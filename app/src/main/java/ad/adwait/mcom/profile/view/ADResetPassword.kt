package ad.adwait.mcom.profile.view

import ad.adwait.mcom.R
import ad.adwait.mcom.registeration.model.ADUserDetails
import and.com.polam.utils.ADBaseActivity
import and.com.polam.utils.MySharedPreference
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_reset_password.*

class ADResetPassword : ADBaseActivity() {

    private var isEdit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_reset_password)

        password_submit.setOnClickListener(View.OnClickListener {

            val cPswd = current_password.text.toString()
            val nPswd = new_password.text.toString()
            val cnPswd = confirm_password.text.toString()

            confirm_password.hideKeyboard()

            if (TextUtils.isEmpty(cPswd)) {
                current_password.error = getString(R.string.empty_password)
            } else if (TextUtils.isEmpty(nPswd)) {
                new_password.error = getString(R.string.empty_password)
            } else if (TextUtils.isEmpty(cnPswd)) {
                confirm_password.error = getString(R.string.empty_password)
            } else if (!isValidPassword(nPswd)) {
                new_password.setError(getString(R.string.err_invalid_password))
            } else if (!isValidPassword(cnPswd)) {
                confirm_password.setError(getString(R.string.err_invalid_password))
            } else if (!nPswd.equals(cnPswd)) {
                new_password.setError(getString(R.string.password_mismatch))
                confirm_password.setError(getString(R.string.password_mismatch))
            } else {
                if (!isNetworkAvailable()) {
                    showMessage(getString(R.string.no_internet), null, true)
                    return@OnClickListener
                }
                progress_layout.visibility = View.VISIBLE
                verifyPassword(cPswd, nPswd)
            }
        })
    }

    private fun verifyPassword(currentPswd: String, password: String) {
        getUserDetails(object : ValueEventListener {

            override fun onDataChange(data: DataSnapshot) {
                if (data.exists()) {
                    var menteeDetails = data.getValue(ADUserDetails::class.java)!!
                    if (menteeDetails != null) {
                        if (currentPswd != (menteeDetails.password)) {
                            progress_layout.visibility = View.GONE
                            showMessage(
                                getString(R.string.current_password_err),
                                null,
                                true
                            )
                        } else if (menteeDetails.password == password) {
                            progress_layout.visibility = View.GONE
                            showMessage(
                                getString(R.string.same_password),
                                null,
                                true
                            )
                        } else {
                            changePassword(password)
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                progress_layout.visibility = View.GONE
            }

        })
    }

    private fun changePassword(password: String) {

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {

            user.updatePassword(password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        updateUser(password)
                    } else {
                        progress_layout.visibility = View.GONE
                        showMessage(
                            getString(R.string.unable_to_reset_password),
                            null, true
                        )
                    }
                }
        } else {
            progress_layout.visibility = View.GONE
            showMessage(
                getString(R.string.unable_to_reset_password),
                null,
                true
            )
        }
    }

    private fun updateUser(password: String) {

        val mFirebaseInstance = FirebaseDatabase.getInstance()

        // get reference to 'users' node
        val mFirebaseDatabase = mFirebaseInstance.getReference(USER_TABLE_NAME)
        mFirebaseDatabase.child(MySharedPreference(applicationContext).getValueString(getString(R.string.userId))!!)
            .updateChildren(getData(password))
            .addOnSuccessListener {
                showMessage(getString(R.string.mobile_reset_success), null, true)
                progress_layout.visibility = View.GONE
                passwordReset()
            }
            .addOnFailureListener {
                progress_layout.visibility = View.GONE
                passwordReset()
            }
    }


    private fun passwordReset() {
        progress_layout.visibility = View.GONE
        registerLogoutListener()
        FirebaseAuth.getInstance().signOut()
        finish()
    }

    override fun onStart() {
        super.onStart()
        val window = this.getWindow()
        window.setLayout(
            (getScreenDetails(false) * 0.9).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    fun getData(password: String): Map<String, Any?> {

        return mapOf(
            "password" to password
        )
    }

    override fun onBackPressed() {
        if (isEdit) {
            super.onBackPressed()
        }
    }
}